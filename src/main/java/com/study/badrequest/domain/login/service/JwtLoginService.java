package com.study.badrequest.domain.login.service;


import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.Member.entity.Member;
import com.study.badrequest.domain.Member.repository.MemberDtoForLogin;
import com.study.badrequest.domain.Member.repository.MemberReadOnlyRepository;
import com.study.badrequest.domain.Member.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.exception.custom_exception.JwtAuthenticationException;
import com.study.badrequest.exception.custom_exception.MemberException;
import com.study.badrequest.domain.login.entity.RefreshToken;
import com.study.badrequest.domain.login.repository.RefreshTokenRepository;
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

    private final MemberReadOnlyRepository memberReadOnlyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Value("${cookie-status.secure}")
    private boolean secure;
    // TODO: 2023/01/02 test

    /**
     * 로그인
     */
    @CustomLogTracer

    public LoginResponse.LoginDto loginProcessing(String email, String password) {
        /**
         * 로그인 실패시 new MemberException(CustomStatus.LOGIN_FAIL) 이메일과 비밀번호중 어느것이 문제인지 숨김
         */
        MemberDtoForLogin memberDtoForLogin = memberReadOnlyRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CustomStatus.LOGIN_FAIL));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDtoForLogin.getUsername(), password);

        Authentication authentication = getAuthentication(authenticationToken);

        TokenDto tokenDto = jwtUtils.generateToken(authentication);

        RefreshToken refreshToken = saveRefreshToken(memberDtoForLogin.getUsername(), tokenDto);

        ResponseCookie cookie = generateResponseCookie(refreshToken);

        return LoginResponse.LoginDto.builder()
                .id(memberDtoForLogin.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(cookie)
                .accessTokenExpired(tokenDto.getAccessTokenExpiredAt())
                .build();

    }

    /**
     * 리프레시 토큰 저장
     */
    @CustomLogTracer
    public RefreshToken saveRefreshToken(String username, TokenDto tokenDto) {

        RefreshToken refreshToken = RefreshToken.createRefresh()
                .username(username)
                .token(tokenDto.getRefreshToken())
                .expiration(tokenDto.getRefreshTokenExpiredTime())
                .build();

        return refreshTokenRepository.save(refreshToken);


    }

    /**
     * security 인증 실패시 BadCredentialsException -> MemberException throw
     * LOGIN_FAIL(1501, "로그인에 실패했습니다.") 응답에 로그인 아이디 혹은 비밀번호 중 어떤것이 잘못되었는지 감추기 위해 통일
     */
    @CustomLogTracer
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
    private ResponseCookie generateResponseCookie(RefreshToken refreshToken) {

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
    @CustomLogTracer
    public LoginResponse.LogoutResult logoutProcessing(String accessToken) {

        checkTokenStatusIsAccess(accessToken);

        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        //1.Refresh Token 확인 없다면 인증기간 만료로 인한 로그아웃
        if (!refreshTokenRepository.existsById(authentication.getName())) {
            throw new JwtAuthenticationException(CustomStatus.ALREADY_LOGOUT);
        }
        //2.Refresh Token 삭제 Refresh 가 존재하지 않는 다면 로그아웃으로 간주
        refreshTokenRepository.deleteById(authentication.getName());

        return new LoginResponse.LogoutResult();
    }


    /**
     * 토큰 재발급
     */
    @CustomLogTracer
    public LoginResponse.LoginDto reissueProcessing(String accessToken, String refreshToken) {

        //1. 토큰 validation
        checkTokenStatusIsDeniedOrError(accessToken);

        checkTokenStatusIsAccess(refreshToken);

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

        ResponseCookie responseCookie = generateResponseCookie(refresh);

        return LoginResponse.LoginDto.builder()
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

    private void checkTokenStatusIsDeniedOrError(String accessToken) {
        log.debug("[JwtLoginService.handleDeniedOrErrorAccessToken]");
        JwtStatus status = jwtUtils.validateToken(accessToken);

        if (status == JwtStatus.DENIED || status == JwtStatus.ERROR) {
            throw new JwtAuthenticationException(CustomStatus.TOKEN_IS_DENIED);
        }
    }

    private void checkTokenStatusIsAccess(String refreshToken) {
        log.debug("[JwtLoginService.checkTokenIsAccess]");
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
        log.debug("[JwtLoginService.loginCheck]");
        return refreshTokenRepository.findById(email).isPresent();
    }

}
