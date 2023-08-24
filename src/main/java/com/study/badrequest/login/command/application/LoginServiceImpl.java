package com.study.badrequest.login.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.login.command.domain.*;


import com.study.badrequest.member.command.domain.values.AccountStatus;
import com.study.badrequest.member.command.domain.values.RegistrationType;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.login.command.interfaces.LoginResponse;
import com.study.badrequest.member.command.domain.model.MemberEventDto;

import com.study.badrequest.common.exception.CustomRuntimeException;

import com.study.badrequest.member.command.domain.repository.MemberRepository;

import com.study.badrequest.utils.cookie.CookieUtils;
import com.study.badrequest.common.status.JwtStatus;
import com.study.badrequest.utils.email.EmailUtils;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.login.command.interfaces.JwtTokenDto;
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

import static com.study.badrequest.common.constants.AuthenticationHeaders.REFRESH_TOKEN_COOKIE;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.member.command.domain.values.RegistrationType.*;
import static com.study.badrequest.utils.authentication.AuthenticationFactory.generateAuthentication;
import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final DisposalAuthenticationRepository disposalAuthenticationRepository;
    private final TemporaryPasswordRepository temporaryPasswordRepository;

    @Override
    @Transactional
    public LoginResponse.LoginDto emailLoginProcessing(String requestedEmail, String password, String ipAddress) {
        log.info("Login By Email requestedEmail: {}", requestedEmail);

        final String email = EmailUtils.convertDomainToLowercase(requestedEmail);

        MemberEntity activeMember = findActiveMemberByEmail(email);

        registrationTypeVerification(activeMember.getRegistrationType(), BAD_REQUEST);

        verifyingPasswordsByAccountStatus(password, activeMember);

        activeMember.assignIpAddress(ipAddress);

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(activeMember.getAuthenticationCode());

        RefreshToken refreshToken = createNewRefreshToken(activeMember, jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill());

        eventPublisher.publishEvent(new MemberEventDto.Login(activeMember.getId(), "이메일 로그인", ipAddress, LocalDateTime.now()));

        return createLoginDto(activeMember, jwtTokenDto, refreshToken);

    }

    private MemberEntity findActiveMemberByEmail(String email) {
        List<MemberEntity> members = null;
        log.info("is true:{}", members.isEmpty());

        List<MemberEntity> activeMembers = findActiveMembers(members);

        emailDuplicationMemberVerification(activeMembers);

        return findActiveMember(activeMembers);
    }

    private void verifyingPasswordsByAccountStatus(String password, MemberEntity activeMember) {
        switch (activeMember.getAccountStatus()) {
            case ACTIVE:
                compareRequestedPasswordWithStored(password, activeMember.getPassword());
                break;
            case USING_TEMPORARY_PASSWORD:
                checkTemporaryPassword(password, activeMember);
                break;
            case USING_NOT_CONFIRMED_EMAIL:
                throw CustomRuntimeException.createWithApiResponseStatus(IS_NOT_CONFIRMED_MAIL);
        }
    }

    private void registrationTypeVerification(RegistrationType targetType, RegistrationType expectedType) {
        if (targetType != expectedType) {
            throw CustomRuntimeException.createWithApiResponseStatus(ALREADY_REGISTERED_BY_OAUTH2);
        }
    }

    private MemberEntity findActiveMember(List<MemberEntity> activeMembers) {
        return activeMembers.stream()
                .findFirst()
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(THIS_IS_NOT_REGISTERED_AS_MEMBER));
    }

    private void emailDuplicationMemberVerification(List<MemberEntity> activeMembers) {
        if (activeMembers.size() > 1) {
            Object array = activeMembers.stream().map(MemberEntity::getId).toArray();
            log.error("Duplicate Email members Occurrence ids: {}", array);
            throw CustomRuntimeException.createWithApiResponseStatus(FOUND_ACTIVE_MEMBERS_WITH_DUPLICATE_EMAILS);
        }
    }

    private List<MemberEntity> findActiveMembers(List<MemberEntity> members) {
        return members.stream()
                .filter(member -> member.getAccountStatus() != AccountStatus.RESIGNED)
                .collect(Collectors.toList());
    }

    private void checkTemporaryPassword(String password, MemberEntity member) {
        //임시 비밀번호 확인
        TemporaryPassword temporaryPassword = temporaryPasswordRepository.findByMember(member)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_TEMPORARY_PASSWORD));

        if (LocalDateTime.now().isAfter(temporaryPassword.getExpiredAt())) {
            throw CustomRuntimeException.createWithApiResponseStatus(IS_EXPIRED_TEMPORARY_PASSWORD);
        }
        //비밀번호 확인
        if (!passwordEncoder.matches(password, temporaryPassword.getPassword())) {
            throw CustomRuntimeException.createWithApiResponseStatus(LOGIN_FAIL);
        }
    }


    @Override
    @Transactional
    public LoginResponse.LoginDto disposableAuthenticationCodeLoginProcessing(String code, String ipAddress) {
        log.info("Login By Disposable AuthenticationCode");

        Optional<DisposableAuthenticationCode> optionalAuthenticationCode = disposalAuthenticationRepository.findByCode(code);

        if (optionalAuthenticationCode.isEmpty()) {
            throw CustomRuntimeException.createWithApiResponseStatus(WRONG_ONE_TIME_CODE);
        }

        DisposableAuthenticationCode authenticationCode = optionalAuthenticationCode.get();
        final Long memberId = authenticationCode.getMember().getId();

        MemberEntity member = findMemberByIdOrElseThrowRuntimeException(memberId, CAN_NOT_FIND_MEMBER_BY_DISPOSABLE_AUTHENTICATION_CODE);
        member.assignIpAddress(ipAddress);

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getAuthenticationCode());

        disposalAuthenticationRepository.deleteById(authenticationCode.getId());

        eventPublisher.publishEvent(new MemberEventDto.Login(member.getId(), "1회용 인증 코드 로그인", ipAddress, LocalDateTime.now()));

        return createLoginDto(member, jwtTokenDto, createNewRefreshToken(member, jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill()));

    }

    private MemberEntity findMemberByIdOrElseThrowRuntimeException(Long memberId, ApiResponseStatus status) {
        return null;
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

        String changeAbleId = jwtUtils.extractChangeableIdInToken(accessToken);

        refreshTokenRepository
                .findById(changeAbleId)
                .ifPresent(refreshTokenRepository::delete);

        MemberEntity member = findMemberByChangeAbleId(changeAbleId);


        SecurityContextHolder.clearContext();
        CookieUtils.deleteCookie(request, response, "JSESSIONID");
        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN_COOKIE);
        request.getSession().invalidate();

        eventPublisher.publishEvent(new MemberEventDto.Logout(member.getId(), "로그아웃", null, LocalDateTime.now()));

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

        String changeableId = jwtUtils.extractChangeableIdInToken(accessToken);

        RefreshToken refresh = findRefreshTokenByChangeableToken(changeableId);

        verifiyingRequestedRefreshToken(refresh.getToken(), refreshToken);

        MemberEntity member = findMemberByChangeAbleId(changeableId);


        refreshTokenRepository.deleteById(refresh.getAuthenticationCode());

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getAuthenticationCode());

        RefreshToken savedRefreshToken = createNewRefreshToken(member, jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill());

        SecurityContextHolder.clearContext();

        return createLoginDto(member, jwtTokenDto, savedRefreshToken);
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
    private RefreshToken findRefreshTokenByChangeableToken(String changeableToken) {
        return refreshTokenRepository.findById(changeableToken)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.ALREADY_LOGOUT));
    }


    private RefreshToken createNewRefreshToken(MemberEntity member, String refreshToken, long expiration) {
        RefreshToken token = RefreshToken.createRefresh()
                .authenticationCode(member.getAuthenticationCode())
                .memberId(member.getId())
                .token(refreshToken)
                .authority(member.getAuthority())
                .expiration(expiration)
                .build();
        return refreshTokenRepository.save(token);
    }

    private MemberEntity findMemberByChangeAbleId(String changeableId) {
        return memberRepository
                .findMemberByAuthenticationCodeAndCreatedAt(changeableId, MemberEntity.getCreatedAtInAuthenticationCode(changeableId))
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public String getDisposableAuthenticationCode(Long memberId) {
        log.info("Get Disposable Authentication Code");
        MemberEntity member = findMemberById(memberId);

        DisposableAuthenticationCode disposableAuthenticationCode = DisposableAuthenticationCode.createDisposableAuthenticationCode(member);

        return disposalAuthenticationRepository.save(disposableAuthenticationCode).getCode();
    }

    private MemberEntity findMemberById(Long memberId) {
        return null;
    }

    private LoginResponse.LoginDto createLoginDto(MemberEntity member, JwtTokenDto jwtTokenDto, RefreshToken refreshToken) {
        return LoginResponse.LoginDto.builder()
                .id(member.getId())
                .accessToken(jwtTokenDto.getAccessToken())
                .refreshCookie(CookieUtils.createRefreshTokenCookie(refreshToken.getToken(), refreshToken.getExpiration()))
                .loggedIn(LocalDateTime.now())
                .build();
    }

    private void compareRequestedPasswordWithStored(String requestedPassword, String storedPassword) {
        if (!passwordEncoder.matches(requestedPassword, storedPassword)) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.LOGIN_FAIL);
        }
    }

    private void verifiyingRequestedRefreshToken(String StoredRefreshToken, String refreshToken) {
        if (!StoredRefreshToken.equals(refreshToken)) {
            throw CustomRuntimeException.createWithApiResponseStatus(REFRESH_TOKEN_IS_DENIED);
        }
    }

    /**
     * 로그아웃 체크
     * 리프레시 토큰이 존재 한다면 로그인 없다면 로그아웃
     */
    @Transactional(readOnly = true)
    public boolean setAuthenticationInContextHolderByChangeableId(String changeableId) {
        log.info("Set Authentication In ContextHolder By ChangeableId ID: {}", changeableId);

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(changeableId);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            Authentication authentication = generateAuthentication(refreshToken.getAuthenticationCode(), refreshToken.getMemberId(), refreshToken.getAuthority());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } else {
            return false;
        }
    }
}
