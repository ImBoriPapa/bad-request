package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.*;
import com.study.badrequest.domain.login.RefreshToken;


import com.study.badrequest.dto.login.LoginResponse;
import com.study.badrequest.event.member.MemberEventDto;

import com.study.badrequest.exception.CustomRuntimeException;

import com.study.badrequest.repository.login.DisposalAuthenticationRepository;
import com.study.badrequest.repository.login.RedisRefreshTokenRepository;
import com.study.badrequest.repository.member.MemberRepository;

import com.study.badrequest.repository.member.TemporaryPasswordRepository;
import com.study.badrequest.utils.cookie.CookieUtils;
import com.study.badrequest.commons.status.JwtStatus;
import com.study.badrequest.utils.email.EmailUtils;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final DisposalAuthenticationRepository disposalAuthenticationRepository;
    private final TemporaryPasswordRepository temporaryPasswordRepository;

    @Override
    @Transactional
    public LoginResponse.LoginDto emailLogin(String requestedEmail, String password, String ipAddress) {
        log.info("이메일 로그인 : {}", requestedEmail);

        final String email = EmailUtils.convertDomainToLowercase(requestedEmail);

        List<Member> members = memberRepository.findMembersByEmail(email);

        List<Member> activeMembers = members.stream()
                .filter(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN)
                .collect(Collectors.toList());

        if (activeMembers.size() > 1) {
            Object array = activeMembers.stream().map(Member::getId).toArray();
            log.error("Duplicate Email members Occurrence ids: {}", array);
            throw new CustomRuntimeException(FOUND_ACTIVE_MEMBERS_WITH_DUPLICATE_EMAILS);
        }

        Member activeMember = activeMembers.stream()
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(THIS_IS_NOT_REGISTERED_AS_MEMBER));

        if (activeMember.getRegistrationType() != RegistrationType.BAD_REQUEST) {
            throw new CustomRuntimeException(ALREADY_REGISTERED_BY_OAUTH2);
        }

        switch (activeMember.getAccountStatus()) {
            case ACTIVE:
                compareRequestedPasswordWithStored(password, activeMember.getPassword());
                break;
            case PASSWORD_IS_TEMPORARY:
                checkTemporaryPassword(password, activeMember);
                break;
            case REQUIRED_MAIL_CONFIRMED:
                throw new CustomRuntimeException(IS_NOT_CONFIRMED_MAIL);
        }

        return getLoginDto(ipAddress, activeMember);

    }

    private void checkTemporaryPassword(String password, Member member) {
        //임시 비밀번호 확인
        TemporaryPassword temporaryPassword = temporaryPasswordRepository.findByMember(member)
                .orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_TEMPORARY_PASSWORD));

        if (LocalDateTime.now().isAfter(temporaryPassword.getExpiredAt())) {
            throw new CustomRuntimeException(IS_EXPIRED_TEMPORARY_PASSWORD);
        }
        //비밀번호 확인
        if (!passwordEncoder.matches(password, temporaryPassword.getPassword())) {
            throw new CustomRuntimeException(LOGIN_FAIL);
        }
    }

    private LoginResponse.LoginDto getLoginDto(String ipAddress, Member member) {
        //토큰 생성
        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());
        //RefreshToken 저장
        RefreshToken refreshToken = createNewRefreshToken(member, jwtTokenDto.getRefreshToken(),jwtTokenDto.getRefreshTokenExpirationMill());
        //요청 IP 저장
        member.setLastLoginIP(ipAddress);

        eventPublisher.publishEvent(new MemberEventDto.Login(member.getId(), "이메일 로그인", ipAddress, LocalDateTime.now()));

        return createLoginDto(member, jwtTokenDto, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse.LoginDto oneTimeAuthenticationCodeLogin(String code, String ipAddress) {
        log.info("1회용 인증 코드로 로그인 ");

        Optional<DisposalAuthenticationCode> optionalAuthenticationCode = disposalAuthenticationRepository.findByCode(code);

        if (optionalAuthenticationCode.isEmpty()) {
            throw new CustomRuntimeException(WRONG_ONE_TIME_CODE);
        }

        DisposalAuthenticationCode authenticationCode = optionalAuthenticationCode.get();

        Member member = memberRepository.findById(authenticationCode.getMember().getId())
                .orElseThrow(() -> new CustomRuntimeException(CAN_NOT_FIND_MEMBER_BY_ONE_TIME_CODE));
        member.setLastLoginIP(ipAddress);

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());

        disposalAuthenticationRepository.deleteById(authenticationCode.getId());

        //After Commit
        eventPublisher.publishEvent(new MemberEventDto.Login(member.getId(), "1회용 인증 코드 로그인", ipAddress, LocalDateTime.now()));

        return createLoginDto(member, jwtTokenDto, createNewRefreshToken(member, jwtTokenDto.getRefreshToken(),jwtTokenDto.getRefreshTokenExpirationMill()));

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

        eventPublisher.publishEvent(new MemberEventDto.Logout(member.getId(), "로그아웃", null, LocalDateTime.now()));

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
        RefreshToken savedRefreshToken = createNewRefreshToken(member, jwtTokenDto.getRefreshToken(),jwtTokenDto.getRefreshTokenExpirationMill());
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


    private RefreshToken createNewRefreshToken(Member member, String refreshToken,long expiration) {
        RefreshToken token = RefreshToken.createRefresh()
                .changeableId(member.getChangeableId())
                .memberId(member.getId())
                .token(refreshToken)
                .authority(member.getAuthority())
                .expiration(expiration)
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
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
        return disposalAuthenticationRepository.save(new DisposalAuthenticationCode(member)).getCode();

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
