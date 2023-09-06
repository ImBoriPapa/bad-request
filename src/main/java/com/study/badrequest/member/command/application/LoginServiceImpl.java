package com.study.badrequest.member.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.login.command.domain.*;


import com.study.badrequest.member.command.domain.RefreshToken;
import com.study.badrequest.member.command.domain.dto.TokenPayLoadDto;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.values.MemberJwtEncodedPayload;
import com.study.badrequest.member.command.domain.values.TokenStatus;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.login.command.interfaces.LoginResponse;
import com.study.badrequest.member.command.domain.events.MemberEventDto;

import com.study.badrequest.common.exception.CustomRuntimeException;

import com.study.badrequest.member.command.domain.repository.MemberRepository;

import com.study.badrequest.utils.cookie.CookieUtils;
import com.study.badrequest.common.status.JwtStatus;
import com.study.badrequest.utils.email.EmailFormatter;
import com.study.badrequest.utils.jwt.JwtPayloadEncoder;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.login.command.interfaces.JwtTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;


import static com.study.badrequest.common.constants.AuthenticationHeaders.REFRESH_TOKEN_COOKIE;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.member.command.domain.values.MemberStatus.*;

import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final MemberPasswordEncoder memberPasswordEncoder;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional
    public LoginResponse.LoginDto emailLoginProcessing(String requestedEmail, String password, String ipAddress) {
        log.info("Login By Email requestedEmail: {}", requestedEmail);
        //이메일 형식 맞춤
        final String email = EmailFormatter.convertDomainToLowercase(requestedEmail);
        //이메일로 회원 조회
        List<Member> members = memberRepository.findMembersByEmail(email);
        //이메일로 회원 조회 결과 없으면 예외
        if (members.isEmpty()) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER);
        }
        //활동중인 회원 조회
        Member activeMember = members.stream()
                .filter(m -> m.getMemberStatus() == ACTIVE)
                .findFirst()
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));


        Member member = activeMember.loginWithEmail(memberPasswordEncoder, password);


        String encodedMemberId = JwtPayloadEncoder.encode(member.getMemberId().getId().toString());
        String encodedAuthority = JwtPayloadEncoder.encode(member.getAuthority().toString());
        String encodedStatus = JwtPayloadEncoder.encode(TokenStatus.ACTIVE.toString());

        MemberJwtEncodedPayload payload = new MemberJwtEncodedPayload(encodedMemberId, encodedAuthority, encodedStatus);
        //TokenDto 생성
        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(payload);

        RefreshToken refreshToken = RefreshToken.createRefreshToken(member.getAuthenticationCode(), jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill());

        return createLoginDto(activeMember.getMemberId().getId(), jwtTokenDto, refreshToken);

    }


    @Override
    @Transactional
    public LoginResponse.LogoutResult logoutProcessing(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout Processing");
        String accessToken = accessTokenResolver(request);

        if (accessToken == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.ACCESS_TOKEN_IS_EMPTY);
        }

        verifyingAccessToken(accessToken);

        String changeAbleId = null;

        refreshTokenRepository
                .findById(changeAbleId)
                .ifPresent(refreshTokenRepository::delete);

        Member member = findMemberByChangeAbleId(changeAbleId);


        SecurityContextHolder.clearContext();
        CookieUtils.deleteCookie(request, response, "JSESSIONID");
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_COOKIE);
        request.getSession().invalidate();

        eventPublisher.publishEvent(new MemberEventDto.Logout(member.getMemberId().getId(), "로그아웃", null, LocalDateTime.now()));

        return new LoginResponse.LogoutResult();
    }

    private void verifyingAccessToken(String accessToken) {
        switch (jwtUtils.validateToken(accessToken)) {
            case DENIED:
                throw CustomRuntimeException.createWithApiResponseStatus(ACCESS_TOKEN_IS_DENIED);
            case EXPIRED:
                throw CustomRuntimeException.createWithApiResponseStatus(ACCESS_TOKEN_IS_EXPIRED);
            case ERROR:
                throw CustomRuntimeException.createWithApiResponseStatus(ACCESS_TOKEN_IS_ERROR);
        }
    }


    @Override
    @Transactional
    public LoginResponse.LoginDto reissueTokenProcessing(String accessToken, String refreshToken) {
        log.info("Reissue Token Processing accessToken={}, refreshToken= {}", accessToken, refreshToken);

        validateAccessToken(accessToken);

        validateRefreshToken(refreshToken);

        String changeableId = null;

        RefreshTokenHash refresh = findRefreshTokenByChangeableToken(changeableId);

        verifiyingRequestedRefreshToken(refresh.getToken(), refreshToken);

        Member member = findMemberByChangeAbleId(changeableId);


        refreshTokenRepository.deleteById(refresh.getId());
        TokenPayLoadDto tokenPayLoadDto = new TokenPayLoadDto(member.getMemberId().getId(), member.getAuthority(), true);
        JwtTokenDto jwtTokenDto = null;

        RefreshToken refreshToken1 = RefreshToken.createRefreshToken(null,null,1L);

        SecurityContextHolder.clearContext();

        return createLoginDto(member.getMemberId().getId(), jwtTokenDto, refreshToken1);
    }

    private void validateRefreshToken(String refreshToken) {
        JwtStatus refreshStatus = jwtUtils.validateToken(refreshToken);

        if (refreshStatus == JwtStatus.DENIED || refreshStatus == JwtStatus.ERROR) {
            throw CustomRuntimeException.createWithApiResponseStatus(REFRESH_TOKEN_IS_DENIED);

        } else if (refreshStatus == JwtStatus.EXPIRED) {
            throw CustomRuntimeException.createWithApiResponseStatus(REFRESH_TOKEN_IS_EXPIRED);
        }
    }

    private void validateAccessToken(String accessToken) {
        JwtStatus accessStatus = jwtUtils.validateToken(accessToken);
        if (accessStatus == JwtStatus.DENIED || accessStatus == JwtStatus.ERROR) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.ACCESS_TOKEN_IS_DENIED);
        }
    }

    /**
     * 리프레시 토큰을 찾지 못할 경우 로그아웃된 계정으로 간주
     */
    private RefreshTokenHash findRefreshTokenByChangeableToken(String changeableToken) {
        return refreshTokenRepository.findById(changeableToken)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.ALREADY_LOGOUT));
    }

    private Member findMemberByChangeAbleId(String changeableId) {
        return memberRepository
                .findMemberByAuthenticationCodeAndCreatedAt(changeableId, MemberEntity.getCreatedAtInAuthenticationCode(changeableId))
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
    }

    private LoginResponse.LoginDto createLoginDto(Long memberId, JwtTokenDto jwtTokenDto, RefreshToken refreshToken) {
        return LoginResponse.LoginDto.builder()
                .id(memberId)
                .accessToken(jwtTokenDto.getAccessToken())
                .refreshCookie(CookieUtils.createRefreshTokenCookie(refreshToken.getToken(), refreshToken.getExpiration()))
                .loggedIn(LocalDateTime.now())
                .build();
    }


    private void verifiyingRequestedRefreshToken(String StoredRefreshToken, String refreshToken) {
        if (!StoredRefreshToken.equals(refreshToken)) {
            throw CustomRuntimeException.createWithApiResponseStatus(REFRESH_TOKEN_IS_DENIED);
        }
    }
}
