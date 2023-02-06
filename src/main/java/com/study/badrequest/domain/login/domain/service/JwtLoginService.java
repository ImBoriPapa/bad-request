package com.study.badrequest.domain.login.domain.service;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.JwtAuthenticationException;
import com.study.badrequest.exception.custom_exception.MemberException;
import com.study.badrequest.domain.login.domain.entity.RefreshToken;
import com.study.badrequest.domain.login.domain.repository.RefreshTokenRepository;
import com.study.badrequest.domain.login.dto.LoginDto;
import com.study.badrequest.utils.jwt.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_COOKIE;
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
    @Value("${cookie-status.secure}")
    private boolean secure;
    // TODO: 2023/01/02 test

    /**
     * 로그인
     */
    @CustomLogger
    public LoginDto loginProcessing(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CustomStatus.LOGIN_FAIL));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getUsername(), password);


        Authentication authentication = getAuthentication(authenticationToken);


        TokenDto tokenDto = jwtUtils.generateToken(authentication);


        RefreshToken refreshToken = setRefreshToken(member, tokenDto);

        ResponseCookie cookie = getResponseCookie(refreshToken);

        return LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(cookie)
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();

    }

    /**
     * 리프레시 토큰 저장
     */
    @CustomLogger
    public RefreshToken setRefreshToken(Member member, TokenDto tokenDto) {

        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(member.getUsername())
                .token(tokenDto.getRefreshToken())
                .expiration(tokenDto.getRefreshTokenExpiredTime())
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    /**
     * security 인증 실패시 BadCredentialsException -> MemberException throw
     * LOGIN_FAIL(1501, "로그인에 실패했습니다.") 응답에 로그인 아이디 혹은 비밀번호 중 어떤것이 잘못되었는지 감추기 위해 통일
     */
    @CustomLogger
    private Authentication getAuthentication(UsernamePasswordAuthenticationToken authenticationToken) {

        final Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new MemberException(CustomStatus.LOGIN_FAIL);
        }
        return authentication;
    }

    /**
     * Https 적용 후 secure false -> true
     * ResponseCookie 생성 운영 환경별로 secure 설정
     */
    private ResponseCookie getResponseCookie(RefreshToken refreshToken) {

        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN_PREFIX + refreshToken.getToken())
                .maxAge(refreshToken.getExpiration())
                .path("/")
                .secure(secure)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }

    /**
     * 로그아웃
     */
    @CustomLogger
    public void logoutProcessing(String accessToken) {

        handleDeniedToken(accessToken);

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        //1.Refresh Token 확인 없다면 인증기간 만료로 인한 로그아웃
        if (!refreshTokenRepository.existsById(authentication.getName())) {
            throw new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT);
        }
        //2.Refresh Token 삭제 Refresh 가 존재하지 않는 다면 로그아웃으로 간주
        refreshTokenRepository.deleteById(authentication.getName());
    }


    /**
     * 토큰 재발급
     */
    @Transactional
    @CustomLogger
    public LoginDto reissueProcessing(String accessToken, String refreshToken) {

        //1. 토큰 validation
        handleDeniedOrErrorAccessToken(accessToken);
        handleDeniedToken(refreshToken);

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        //2. 존재하는 회원인지 확인
        Member member = findMemberByUsername(authentication.getName());
        //3. Refresh 토큰이 존재하지 않으면 로그아웃으로 간주
        RefreshToken refresh = refreshTokenRepository.findById(member.getUsername())
                .orElseThrow(() -> new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT));
        //4. 저장된 리프레시 토큰과 요청한 토큰을 비교
        compareRefreshWithStored(refresh.getToken(), refreshToken);
        //6. 토큰 생성
        TokenDto tokenDto = jwtUtils.generateToken(authentication);
        //7. 토큰 갱신
        replaceRefresh(refresh, tokenDto);

        ResponseCookie responseCookie = getResponseCookie(refresh);

        return LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(responseCookie)
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();
    }

    public void replaceRefresh(RefreshToken refresh, TokenDto tokenDto) {
        refresh.replaceToken(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpiredTime());
        refreshTokenRepository.save(refresh);
    }

    private static void compareRefreshWithStored(String StoredRefresh, String refreshToken) {
        if (!StoredRefresh.equals(refreshToken)) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
    }

    public Member findMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));
    }

    private void handleDeniedOrErrorAccessToken(String accessToken) {
        log.info("[JwtLoginService.handleDeniedOrErrorAccessToken]");
        JwtStatus status = jwtUtils.validateToken(accessToken);

        if (status == JwtStatus.DENIED || status == JwtStatus.ERROR) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
    }

    private void handleDeniedToken(String refreshToken) {
        log.info("[JwtLoginService.handleDeniedToken]");
        if (jwtUtils.validateToken(refreshToken) != JwtStatus.ACCESS) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
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
