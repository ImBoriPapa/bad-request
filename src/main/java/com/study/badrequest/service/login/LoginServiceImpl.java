package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.RefreshToken;

import com.study.badrequest.domain.member.AuthenticationCode;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.login.LoginResponse;
import com.study.badrequest.event.member.MemberEventDto;

import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import com.study.badrequest.repository.login.AuthenticationCodeRepository;
import com.study.badrequest.repository.login.RedisRefreshTokenRepository;
import com.study.badrequest.repository.member.MemberRepository;

import com.study.badrequest.utils.cookie.CookieUtils;
import com.study.badrequest.commons.status.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.dto.jwt.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.study.badrequest.commons.constants.AuthenticationHeaders.REFRESH_TOKEN_COOKIE;
import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static com.study.badrequest.utils.authentication.AuthenticationFactory.generateAuthentication;
import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {
    private final MemberRepository memberRepository;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationCodeRepository authenticationCodeRepository;

    @Override
    @Transactional
    public LoginResponse.LoginDto emailLogin(String email, String password, String ipAddress) {
        log.info("이메일 로그인 : {}", email);
        //이메일 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.LOGIN_FAIL));
        //비밀번호 확인
        compareRequestedPasswordWithStored(password, member.getPassword());
        //이메일 인증 여부 확인
        member.checkConfirmedMail();
        //임시 비밀번호 여부 확인
        member.checkTemporaryPassword();
        //토큰 생성
        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());
        //RefreshToken 저장
        RefreshToken refreshToken = createNewRefreshToken(member, jwtTokenDto);
        //요청 IP 저장
        member.setLastLoginIP(ipAddress);

        eventPublisher.publishEvent(new MemberEventDto.Login(member, "일반 로그인", LocalDateTime.now()));

        return createLoginDto(member, jwtTokenDto, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse.LoginDto oneTimeAuthenticationCodeLogin(String code, String ipAddress) {
        log.info("1회용 인증 코드로 로그인 ");

        Optional<AuthenticationCode> optionalAuthenticationCode = authenticationCodeRepository.findByCode(code);

        if (optionalAuthenticationCode.isEmpty()) {
            throw new CustomRuntimeException(WRONG_ONE_TIME_CODE);
        }

        AuthenticationCode authenticationCode = optionalAuthenticationCode.get();

        Member member = memberRepository.findById(authenticationCode.getMember().getId())
                .orElseThrow(() -> new CustomRuntimeException(CAN_NOT_FIND_MEMBER_BY_ONE_TIME_CODE));
        member.setLastLoginIP(ipAddress);

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());

        authenticationCodeRepository.deleteById(authenticationCode.getId());

        //After Commit
        eventPublisher.publishEvent(new MemberEventDto.Login(member, "1회용 인증 코드 로그인", LocalDateTime.now()));

        return createLoginDto(member, jwtTokenDto, createNewRefreshToken(member, jwtTokenDto));

    }

    @Override
    @Transactional
    public LoginResponse.LogoutResult logoutProcessing(HttpServletRequest request, HttpServletResponse response) {
        log.info("logoutProcessing accessToken");
        String accessToken = accessTokenResolver(request);

        if (accessToken == null) {
            throw new CustomRuntimeException(ApiResponseStatus.TOKEN_IS_EMPTY);
        }

        switch (jwtUtils.validateToken(accessToken)) {
            case ACCESS:
                break;
            case DENIED:
                throw new CustomRuntimeException(PERMISSION_DENIED);
            case EXPIRED:
                throw new CustomRuntimeException(TOKEN_IS_EXPIRED);
            case ERROR:
                throw new CustomRuntimeException(TOKEN_IS_ERROR);
        }

        String changeAbleId = jwtUtils.getChangeableIdInToken(accessToken);

        redisRefreshTokenRepository.findById(changeAbleId).ifPresent(redisRefreshTokenRepository::delete);

        Member member = findMemberByChangeAbleId(changeAbleId);
        member.replaceChangeableId();

        SecurityContextHolder.clearContext();
        CookieUtils.deleteCookie(request, response, "JSESSIONID");
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_COOKIE);
        request.getSession().invalidate();

        eventPublisher.publishEvent(new MemberEventDto.Logout(member, "로그아웃", LocalDateTime.now()));

        return new LoginResponse.LogoutResult();
    }


    @Override
    @Transactional
    public LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken) {
        log.info("토큰 재발급 시작 accessToken={}, refreshToken= {}", accessToken, refreshToken);
        //1 AccessToken 검증
        validateAccessToken(accessToken);
        //2 RefreshToken 검증
        validateRefreshToken(refreshToken);

        //3. Refresh 토큰으로 조회시 정보가 없으면 로그아웃으로 간주
        String changeableId = jwtUtils.getChangeableIdInToken(accessToken);

        RefreshToken refresh = findRefreshTokenByChangeableToken(changeableId);
        //4. 저장된 리프레시 토큰과 요청한 토큰을 비교
        compareRefreshTokenRequestedWithStored(refresh.getToken(), refreshToken);
        //5. 존재하는 회원인지 확인
        Member member = findMemberByChangeAbleId(changeableId);
        //6. changeableId 변경
        member.replaceChangeableId();
        //7. 기존 Refresh 토큰 삭제
        redisRefreshTokenRepository.deleteById(refresh.getChangeableId());
        //8. 새로운 토큰 생성
        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());
        //9  새로운 Refresh 토큰 저장
        RefreshToken savedRefreshToken = createNewRefreshToken(member, jwtTokenDto);
        //10. 기존 인증정보 삭제
        SecurityContextHolder.clearContext();

        return createLoginDto(member, jwtTokenDto, savedRefreshToken);
    }

    private void validateRefreshToken(String refreshToken) {
        JwtStatus refreshStatus = jwtUtils.validateToken(refreshToken);
        if (refreshStatus == JwtStatus.DENIED || refreshStatus == JwtStatus.ERROR) {
            throw new CustomRuntimeException(ApiResponseStatus.TOKEN_IS_DENIED);
        } else if (refreshStatus == JwtStatus.EXPIRED) {
            throw new CustomRuntimeException(ApiResponseStatus.TOKEN_IS_EXPIRED);
        }
    }

    private void validateAccessToken(String accessToken) {

        JwtStatus accessStatus = jwtUtils.validateToken(accessToken);
        if (accessStatus == JwtStatus.DENIED || accessStatus == JwtStatus.ERROR) {
            throw new CustomRuntimeException(ApiResponseStatus.TOKEN_IS_DENIED);
        }
    }

    private RefreshToken findRefreshTokenByChangeableToken(String changeableToken) {
        return redisRefreshTokenRepository.findById(changeableToken)
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.ALREADY_LOGOUT));
    }


    private RefreshToken createNewRefreshToken(Member member, JwtTokenDto jwtTokenDto) {
        RefreshToken token = RefreshToken.createRefresh()
                .changeableId(member.getChangeableId())
                .memberId(member.getId())
                .token(jwtTokenDto.getRefreshToken())
                .authority(member.getAuthority())
                .expiration(jwtTokenDto.getRefreshTokenExpirationMill())
                .build();
        return redisRefreshTokenRepository.save(token);
    }

    private Member findMemberByChangeAbleId(String changeableId) {
        return memberRepository
                .findMemberByChangeableIdAndCreateDateTimeIndex(changeableId, Member.getCreatedAtInChangeableId(changeableId))
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public String getOneTimeAuthenticationCode(Long memberId) {
        log.info("일회용 인증 코드 생성");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));

        return authenticationCodeRepository.save(AuthenticationCode.createOnetimeAuthenticationCode(member)).getCode();

    }


    private LoginResponse.LoginDto createLoginDto(Member member, JwtTokenDto jwtTokenDto, RefreshToken refreshToken) {
        return LoginResponse.LoginDto.builder()
                .id(member.getId())
                .accessToken(jwtTokenDto.getAccessToken())
                .refreshCookie(CookieUtils.createRefreshTokenCookie(refreshToken.getToken(), refreshToken.getExpiration()))
                .loggedIn(LocalDateTime.now())
                .build();
    }

    private void compareRequestedPasswordWithStored(String requestedPassword, String storedPassword) {
        if (!passwordEncoder.matches(requestedPassword, storedPassword)) {
            throw new CustomRuntimeException(ApiResponseStatus.LOGIN_FAIL);
        }
    }

    private void compareRefreshTokenRequestedWithStored(String StoredRefreshToken, String refreshToken) {
        if (!StoredRefreshToken.equals(refreshToken)) {
            throw new CustomRuntimeException(ApiResponseStatus.TOKEN_IS_DENIED);
        }
    }

    /**
     * 로그아웃 체크
     * 리프레시 토큰이 존재 한다면 로그인 없다면 로그아웃
     */
    @Transactional(readOnly = true)
    public boolean setAuthenticationInContextHolderByChangeableId(String changeableId) {
        log.info("Set Authentication In ContextHolder By ChangeableId ID: {}", changeableId);

        Optional<RefreshToken> optionalRefreshToken = redisRefreshTokenRepository.findById(changeableId);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            Authentication authentication = generateAuthentication(refreshToken.getChangeableId(), refreshToken.getMemberId(), refreshToken.getAuthority());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } else {
            return false;
        }
    }
}
