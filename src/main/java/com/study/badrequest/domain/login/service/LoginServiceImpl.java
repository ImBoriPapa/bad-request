package com.study.badrequest.domain.login.service;


import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.commons.exception.custom_exception.JwtAuthenticationException;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.login.repository.redisRefreshTokenRepository;
import com.study.badrequest.domain.member.repository.query.MemberLoginInformation;
import com.study.badrequest.domain.member.repository.query.MemberSimpleInformation;
import com.study.badrequest.utils.jwt.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_COOKIE;
import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {
    private final MemberRepository memberRepository;
    private final redisRefreshTokenRepository redisRefreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    @Value("${cookie-status.secure}")
    private boolean isSecure;


    @CustomLogTracer
    @Override
    @Transactional
    public LoginResponse.LoginDto login(String email, String password) {
        log.info("Login Request Email= {}, Password= Protected", email);
        return createLoginDto(email, password);
    }


    @CustomLogTracer
    @Override
    @Transactional
    public LoginResponse.LogoutResult logout(String accessToken) {
        log.info("logoutProcessing accessToken= {}", accessToken);

        String username = jwtUtils.getUsernameInToken(accessToken);

        throwExceptionIfTokenIsNotAccess(accessToken);

        removeRefreshToken(username);

        removeSpringSecuritySession();

        replaceUsername(username);

        return new LoginResponse.LogoutResult();
    }

    private void throwExceptionIfTokenIsNotAccess(String accessToken) {
        if (jwtUtils.validateToken(accessToken) != JwtStatus.ACCESS) {
            throw new JwtAuthenticationException(CustomStatus.PERMISSION_DENIED);
        }
    }

    @CustomLogTracer
    @Override
    @Transactional
    public LoginResponse.LoginDto reissueToken(String accessToken, String refreshToken) {
        log.info("reissueProcessing accessToken={}, refreshToken= {}", accessToken, refreshToken);
        //1. 토큰 상태가 Access or Expired 가 아니면 exception
        throwExceptionIfTokenIsNotAccessOrExpired(accessToken, refreshToken);

        //2. Refresh 토큰이 존재하지 않으면 로그아웃 처리
        RefreshToken refresh = findRefreshByAccessToken(accessToken);

        //3. 저장된 리프레시 토큰과 요청한 토큰을 비교
        compareRefreshTokenRequestedWithStored(refresh.getToken(), refreshToken);

        //4. 존재하는 회원인지 확인
        MemberSimpleInformation loginInformation = findMemberInformation(refresh.getUsername(), refresh.getAuthority());

        //6. 토큰 생성
        TokenDto tokenDto = jwtUtils.generateJwtTokens(loginInformation.getUsername());

        //7. 토큰 갱신
        replaceRefreshToken(refresh, tokenDto);

        return LoginResponse.LoginDto.builder()
                .id(loginInformation.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(createResponseCookie(refresh))
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();
    }

    private RefreshToken findRefreshByAccessToken(String accessToken) {
        return redisRefreshTokenRepository.findById(jwtUtils.getUsernameInToken(accessToken))
                .orElseThrow(() -> new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT));
    }


    private LoginResponse.LoginDto createLoginDto(String email, String password) {

        MemberLoginInformation information = findMemberInformationEmail(email);

        comparePasswordRequestedWithStored(password, information.getPassword());

        TokenDto tokenDto = jwtUtils.generateJwtTokens(information.getUsername());

        RefreshToken refreshToken = storeRefreshToken(information.getUsername(), information.getAuthority(), tokenDto);

        return LoginResponse.LoginDto.builder()
                .id(information.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(createResponseCookie(refreshToken))
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();
    }

    private void comparePasswordRequestedWithStored(String requestedPassword, String storedPassword) {
        if (!passwordEncoder.matches(requestedPassword, storedPassword)) {
            throw new MemberException(CustomStatus.LOGIN_FAIL);
        }
    }

    private MemberLoginInformation findMemberInformationEmail(String email) {
        return memberRepository.findLoginInformationByEmail(email)
                .orElseThrow(() -> new MemberException(CustomStatus.LOGIN_FAIL));
    }

    /**
     * 회원 식별자, username, 권한 정보만 조회
     * 조회시 권한정보로 인덱싱
     */
    private MemberSimpleInformation findMemberInformation(String username, Authority authority) {
        log.info("findMemberInformation {}, {}", username, authority);
        return memberRepository
                .findByUsernameAndAuthority(username, authority)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
    }

    /**
     * 리프레시 토큰 저장
     * Redis
     *
     * @return RefreshToken
     */
    @CustomLogTracer
    private RefreshToken storeRefreshToken(String username, Authority authority, TokenDto tokenDto) {
        log.info("refresh token save");
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(tokenDto.getRefreshToken())
                .authority(authority)
                .expiration(tokenDto.getRefreshTokenExpirationMill())
                .build();
        return redisRefreshTokenRepository.save(refreshToken);
    }


    /**
     * Https 적용 후 secure false -> true
     * ResponseCookie 생성 운영 환경별로 secure 설정
     */
    private ResponseCookie createResponseCookie(RefreshToken refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN_PREFIX + refreshToken.getToken())
                .maxAge(refreshToken.getExpiration())
                .path("/")
                .secure(isSecure)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }


    private void removeSpringSecuritySession() {
        SecurityContextHolder.clearContext();
    }

    private void replaceUsername(String username) {
        memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER))
                .replaceUsername();
    }

    private void removeRefreshToken(String username) {
        checkIsLogout(username);
        redisRefreshTokenRepository.deleteById(username);
    }

    private void checkIsLogout(String username) {
        //Refresh Token 이 없다면 인증기간 만료로 인한 로그아웃
        if (!redisRefreshTokenRepository.existsById(username)) {
            throw new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT);
        }
    }

    private void throwExceptionIfTokenIsNotAccessOrExpired(String accessToken, String refreshToken) {
        JwtStatus accessStatus = jwtUtils.validateToken(accessToken);
        if (accessStatus == JwtStatus.DENIED || accessStatus == JwtStatus.ERROR) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }

        JwtStatus refreshStatus = jwtUtils.validateToken(refreshToken);
        if (refreshStatus == JwtStatus.DENIED || refreshStatus == JwtStatus.ERROR) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }

        if (refreshStatus == JwtStatus.EXPIRED) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_EXPIRED);
        }
    }

    @Transactional
    public void replaceRefreshToken(RefreshToken refresh, TokenDto tokenDto) {
        refresh.replaceToken(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationMill());
        redisRefreshTokenRepository.save(refresh);
    }

    private  void compareRefreshTokenRequestedWithStored(String StoredRefreshToken, String refreshToken) {
        if (!StoredRefreshToken.equals(refreshToken)) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
    }

    /**
     * 로그아웃 체크
     * 리프레시 토큰이 존재 한다면 로그인 없다면 로그아웃
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> loginCheckWithUsername(String username) {
        log.debug("[JwtLoginService.loginCheck]");
        return redisRefreshTokenRepository.findById(username);
    }

}
