package com.study.badrequest.login.domain.service;


import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.MemberException;
import com.study.badrequest.commons.exception.TokenException;
import com.study.badrequest.login.domain.entity.RefreshToken;
import com.study.badrequest.login.domain.repository.RefreshTokenRepository;
import com.study.badrequest.utils.JwtStatus;
import com.study.badrequest.utils.JwtDto;
import com.study.badrequest.utils.JwtUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.study.badrequest.commons.consts.JwtTokenHeader.TOKEN_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class JwtLoginService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // TODO: 2023/01/02 test

    public LoginDto loginProcessing(String email, String password) {
        log.info("[JwtLoginService.loginProcessing]");

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(CustomStatus.LOGIN_FAIL));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(CustomStatus.LOGIN_FAIL);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtDto jwtDto = jwtUtils.generateToken(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(member.getId())
                .token(jwtDto.getRefreshToken())
                .isLogin(true)
                .build();
        refreshTokenRepository.save(refreshToken);

        return LoginDto.builder()
                .id(member.getId())
                .accessToken(TOKEN_PREFIX + jwtDto.getAccessToken())
                .refreshToken(TOKEN_PREFIX + jwtDto.getRefreshToken())
                .accessTokenExpired(jwtUtils.getExpired(jwtDto.getAccessToken()))
                .build();

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginDto {
        private Long id;
        private String accessToken;
        private String refreshToken;
        private Date accessTokenExpired;
    }

    public LoginDto reissue(String accessToken, String refreshToken) {

        if (jwtUtils.validateToken(refreshToken) == JwtStatus.DENIED) {
            throw new TokenException(CustomStatus.ERROR);
        }

        Authentication authentication = jwtUtils.getAuthentication(accessToken);


        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(""));

        RefreshToken refresh = refreshTokenRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException(""));


        if (refresh == null) {
            throw new IllegalArgumentException("로그아웃");
        }

        if (refresh.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("토큰 불일치");
        }

        JwtDto jwtDto = jwtUtils.generateToken(authentication);

        refresh.replaceToken(jwtDto.getRefreshToken());
        refreshTokenRepository.save(refresh);

        return LoginDto.builder()
                .id(member.getId())
                .accessToken(TOKEN_PREFIX + jwtDto.getAccessToken())
                .refreshToken(TOKEN_PREFIX + jwtDto.getRefreshToken())
                .accessTokenExpired(jwtUtils.getExpired(jwtDto.getAccessToken()))
                .build();
    }


    private void createRefresh(Long memberId, String refreshToken) {
        log.info("[JwtLoginService.createRefresh]");
        RefreshToken token = RefreshToken.builder()
                .id(memberId)
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(token);
    }

    private void replaceRefresh(RefreshToken refresh, String refreshToken) {
        log.info("[JwtLoginService.replaceRefresh]");
        refresh.replaceToken(refreshToken);
        refreshTokenRepository.save(refresh);
    }

}
