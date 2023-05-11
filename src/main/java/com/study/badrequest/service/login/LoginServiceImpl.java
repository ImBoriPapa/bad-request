package com.study.badrequest.service.login;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.RefreshToken;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.login.LoginResponse;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.exception.BasicCustomException;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.exception.custom_exception.JwtAuthenticationExceptionBasic;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import com.study.badrequest.repository.login.RedisRefreshTokenRepository;
import com.study.badrequest.repository.member.MemberRepository;

import com.study.badrequest.repository.member.query.MemberSimpleInformation;
import com.study.badrequest.utils.cookie.CookieFactory;
import com.study.badrequest.utils.jwt.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_MATCH_ONE_TIME_CODE;
import static com.study.badrequest.commons.response.ApiResponseStatus.WRONG_ONE_TIME_CODE;
import static com.study.badrequest.utils.authentication.AuthenticationFactory.generateAuthentication;


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

    @Override
    @Transactional
    public LoginResponse.LoginDto emailLogin(String email, String password, String ipAddress) {
        log.info("이메일 로그인 : {}", email);
        //이메일 확인
        Member member = memberRepository.findByEmailAndDomainName(email, Member.extractDomainFromEmail(email))
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.LOGIN_FAIL));
        //비밀번호 확인
        compareRequestedPasswordWithStored(password, member.getPassword());
        //이메일 인증 여부 확인
        member.checkConfirmedMail();
        //임시 비밀번호 여부 확인
        member.checkTemporaryPassword();
        //토큰 생성
        TokenDto tokenDto = jwtUtils.generateJwtTokens(member.getUsername());
        //RefreshToken 저장
        RefreshToken refreshToken = storeRefreshToken(member.getUsername(), member.getId(), member.getAuthority(), tokenDto);
        //요청 IP 저장
        member.setLastLoginIP(ipAddress);

        eventPublisher.publishEvent(new MemberEventDto.Login(member, "일반 로그인", LocalDateTime.now()));

        return createLoginDto(member, tokenDto, refreshToken);
    }

    @Override
    @Transactional
    public LoginResponse.LoginDto oneTimeAuthenticationCodeLogin(String code, String ipAddress) {
        log.info("1회용 인증 코드로 로그인 ");
        Member member = memberRepository.findByOneTimeAuthenticationCode(code)
                .orElseThrow(() -> new CustomRuntimeException(WRONG_ONE_TIME_CODE));

        if (!member.getOneTimeAuthenticationCode().equals(code)) {
            SecurityContextHolder.clearContext();
            throw new CustomRuntimeException(WRONG_ONE_TIME_CODE);
        }

        if (member.getAbleUseOneTimeAuthenticationCode()) {
            SecurityContextHolder.clearContext();
            throw new CustomRuntimeException(NOT_MATCH_ONE_TIME_CODE);
        }

        member.useOneTimeAuthenticationCode();

        member.setLastLoginIP(ipAddress);

        TokenDto tokenDto = jwtUtils.generateJwtTokens(member.getUsername());
        //After Commit
        eventPublisher.publishEvent(new MemberEventDto.Login(member, "일반 로그인", LocalDateTime.now()));

        return createLoginDto(member, tokenDto, storeRefreshToken(member.getUsername(), member.getId(), member.getAuthority(), tokenDto));

    }

    @Override
    @Transactional
    public LoginResponse.LogoutResult logoutProcessing(String accessToken, Cookie cookie) {
        log.info("logoutProcessing accessToken= {}", accessToken);

        String username = jwtUtils.getUsernameInToken(accessToken);

        throwExceptionIfTokenIsNotAccess(accessToken);

        removeRefreshToken(username);

        removeSpringSecuritySession();

        Member member = replaceUsername(username);

        eventPublisher.publishEvent(new MemberEventDto.Logout(member, "로그아웃", LocalDateTime.now()));

        return new LoginResponse.LogoutResult();
    }

    private void throwExceptionIfTokenIsNotAccess(String accessToken) {
        if (jwtUtils.validateToken(accessToken) != JwtStatus.ACCESS) {
            throw new JwtAuthenticationExceptionBasic(ApiResponseStatus.PERMISSION_DENIED);
        }
    }


    @Override
    @Transactional
    public LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken) {
        log.info("토큰 재발급 시작 accessToken={}, refreshToken= {}", accessToken, refreshToken);
        //1. 토큰 상태가 Access or Expired 가 아니면 exception
        throwExceptionIfTokenIsNotAccessOrExpired(accessToken, refreshToken);

        //2. Refresh 토큰이 존재하지 않으면 로그아웃 처리
        RefreshToken refresh = redisRefreshTokenRepository.findById(jwtUtils.getUsernameInToken(accessToken))
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.ALREADY_LOGOUT));

        //3. 저장된 리프레시 토큰과 요청한 토큰을 비교
        compareRefreshTokenRequestedWithStored(refresh.getToken(), refreshToken);

        //4. 존재하는 회원인지 확인
        Member member = memberRepository.findMemberByUsername(jwtUtils.getUsernameInToken(accessToken))
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
        //5. username 변경
        member.replaceUsername();
        //6. 토큰 생성
        TokenDto tokenDto = jwtUtils.generateJwtTokens(member.getUsername());

        //7. 토큰 갱신
        redisRefreshTokenRepository.deleteById(refresh.getUsername());

        RefreshToken token = RefreshToken.createRefresh()
                .username(member.getUsername())
                .memberId(member.getId())
                .token(tokenDto.getRefreshToken())
                .authority(member.getAuthority())
                .expiration(tokenDto.getRefreshTokenExpirationMill())
                .build();

        RefreshToken savedRefreshToken = redisRefreshTokenRepository.save(token);
        //8. 기존 인증정보 삭제
        SecurityContextHolder.clearContext();

        return LoginResponse.LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(CookieFactory.createRefreshTokenCookie(savedRefreshToken.getToken(), savedRefreshToken.getExpiration()))
                .loggedIn(tokenDto.getAccessTokenExpiredAt())
                .build();
    }

    @Override
    @Transactional
    public String getTemporaryAuthenticationCode(Long memberId) {
        log.info("일회용 인증 코드 생성");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));
        return member.createOneTimeAuthenticationCode();
    }


    private LoginResponse.LoginDto createLoginDto(Member member, TokenDto tokenDto, RefreshToken refreshToken) {
        return LoginResponse.LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(CookieFactory.createRefreshTokenCookie(refreshToken.getToken(), refreshToken.getExpiration()))
                .loggedIn(LocalDateTime.now())
                .build();
    }


    private void compareRequestedPasswordWithStored(String requestedPassword, String storedPassword) {
        if (!passwordEncoder.matches(requestedPassword, storedPassword)) {
            throw new BasicCustomException(ApiResponseStatus.LOGIN_FAIL);
        }
    }

    private RefreshToken storeRefreshToken(String username, Long memberId, Authority authority, TokenDto tokenDto) {
        log.info("Refresh Token 저장");
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .memberId(memberId)
                .token(tokenDto.getRefreshToken())
                .authority(authority)
                .expiration(tokenDto.getRefreshTokenExpirationMill())
                .build();
        return redisRefreshTokenRepository.save(refreshToken);
    }

    private void removeSpringSecuritySession() {
        SecurityContextHolder.clearContext();
    }

    private Member replaceUsername(String username) {
        Member member = memberRepository.findMemberByUsername(username)
                .orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));
        member.replaceUsername();
        return member;
    }

    private void removeRefreshToken(String username) {
        checkIsLogout(username);
        redisRefreshTokenRepository.deleteById(username);
    }

    private void checkIsLogout(String username) {
        //Refresh Token 이 없다면 인증기간 만료로 인한 로그아웃
        if (!redisRefreshTokenRepository.existsById(username)) {
            throw new JwtAuthenticationExceptionBasic(ApiResponseStatus.ALREADY_LOGOUT);
        }
    }

    private void throwExceptionIfTokenIsNotAccessOrExpired(String accessToken, String refreshToken) {
        JwtStatus accessStatus = jwtUtils.validateToken(accessToken);
        if (accessStatus == JwtStatus.DENIED || accessStatus == JwtStatus.ERROR) {
            throw new JwtAuthenticationExceptionBasic(ApiResponseStatus.TOKEN_IS_DENIED);
        }

        JwtStatus refreshStatus = jwtUtils.validateToken(refreshToken);
        if (refreshStatus == JwtStatus.DENIED || refreshStatus == JwtStatus.ERROR) {
            throw new JwtAuthenticationExceptionBasic(ApiResponseStatus.TOKEN_IS_DENIED);
        }

        if (refreshStatus == JwtStatus.EXPIRED) {
            throw new JwtAuthenticationExceptionBasic(ApiResponseStatus.TOKEN_IS_EXPIRED);
        }
    }


    private void compareRefreshTokenRequestedWithStored(String StoredRefreshToken, String refreshToken) {
        if (!StoredRefreshToken.equals(refreshToken)) {
            throw new JwtAuthenticationExceptionBasic(ApiResponseStatus.TOKEN_IS_DENIED);
        }
    }

    /**
     * 로그아웃 체크
     * 리프레시 토큰이 존재 한다면 로그인 없다면 로그아웃
     */
    @Transactional(readOnly = true)
    public boolean setAuthenticationInContextHolderByUsername(String username) {
        log.info("Set Authentication In ContextHolder By Username username: {}", username);
        Optional<RefreshToken> optionalRefreshToken = redisRefreshTokenRepository.findById(username);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            Authentication authentication = generateAuthentication(refreshToken.getUsername(), refreshToken.getMemberId(), refreshToken.getAuthority());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } else {
            return false;
        }
    }
}
