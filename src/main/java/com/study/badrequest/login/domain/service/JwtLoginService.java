package com.study.badrequest.login.domain.service;


import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.JwtAuthenticationException;
import com.study.badrequest.exception.MemberException;
import com.study.badrequest.login.domain.entity.RefreshToken;
import com.study.badrequest.login.domain.repository.RefreshTokenRepository;
import com.study.badrequest.login.dto.LoginDto;
import com.study.badrequest.utils.JwtStatus;
import com.study.badrequest.utils.JwtUtils;
import com.study.badrequest.utils.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JwtLoginService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // TODO: 2023/01/02 test

    /**
     * 로그인
     */
    public LoginDto loginProcessing(String email, String password) {
        log.info("[JwtLoginService.loginProcessing]");
        //1. 이메일 중복 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CustomStatus.LOGIN_FAIL));
        //2. authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        //3. Security 회원 검증 authenticate() -> JwtUserDetailService.loadByUsername()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //4. accessToken, refreshToken 생성
        TokenDto tokenDto = jwtUtils.generateToken(authentication);

        //5. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.createRefresh()
                .email(member.getUsername())
                .token(tokenDto.getRefreshToken())
                .expiration(tokenDto.getRefreshTokenExpiredTime())
                .isLogin(true)
                .build();
        refreshTokenRepository.save(refreshToken);
        //6. RefreshToken 생성

        ResponseCookie cookie = getResponseCookie(refreshToken);

        return LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(cookie)
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();

    }

    /**
     * Https 적용 후 secure false -> true
     */
    private ResponseCookie getResponseCookie(RefreshToken refreshToken) {

        return ResponseCookie.from("Refresh", REFRESH_TOKEN_PREFIX + refreshToken.getToken())
                .maxAge(refreshToken.getExpiration())
                .path("/")
                .secure(false)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }

    /**
     * 로그아웃
     */
    public void logoutProcessing(String accessToken) {
        log.info("[JwtLoginService.logoutProcessing]");
        JwtStatus jwtStatus = jwtUtils.validateToken(accessToken);

        deniedTokenHandle(jwtStatus);

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        //1.Refresh Token 확인 없다면 인증기간 만료로 인한 로그아웃
        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT));
        //2.Refresh Token 삭제 Refresh 가 존재하지 않는 다면 로그아웃으로 간주
        refreshTokenRepository.deleteById(refreshToken.getEmail());
    }

    private void deniedTokenHandle(JwtStatus jwtStatus) {
        if (jwtStatus != JwtStatus.ACCESS) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
    }

    /**
     * 토큰 재발급
     */
    public LoginDto reissueProcessing(String accessToken, String refreshToken) {
        //1. 토큰 validation
        JwtStatus accessStatus = jwtUtils.validateToken(accessToken);
        JwtStatus refreshStatus = jwtUtils.validateToken(refreshToken);
        //2. 토큰이 access 가 아니면 exception
        deniedTokenHandle(accessStatus);
        deniedTokenHandle(refreshStatus);

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        //3. 존재하는 회원인지 확인
        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
        //4. Refresh 토큰이 존재하지 않으면 로그아웃으로 간주
        RefreshToken refresh = refreshTokenRepository.findById(member.getUsername())
                .orElseThrow(() -> new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT));
        //5. 저장된 리프레시 토큰과 요청한 토큰을 비교
        if (!refresh.getToken().equals(refreshToken)) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
        //6. 토큰 생성
        TokenDto tokenDto = jwtUtils.generateToken(authentication);
        //7. 토큰 갱신
        refresh.replaceToken(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpiredTime());

        refreshTokenRepository.save(refresh);

        ResponseCookie responseCookie = getResponseCookie(refresh);

        return LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(responseCookie)
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();
    }

    /**
     * 로그아웃 체크
     * 리프레시 토큰이 존재 한다면 로그인 없다면 로그아웃
     */
    @Transactional(readOnly = true)
    public boolean loginCheck(String email) {
        log.info("[LOGIN CHECK]");
        return refreshTokenRepository.findById(email).isPresent();
    }

}
