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
import static com.study.badrequest.domain.member.RegistrationType.*;
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
    public LoginResponse.LoginDto emailLoginProcessing(String requestedEmail, String password, String ipAddress) {
        log.info("Login By Email requestedEmail: {}", requestedEmail);

        final String email = EmailUtils.convertDomainToLowercase(requestedEmail);

        Member activeMember = findActiveMemberByEmail(email);

        registrationTypeVerification(activeMember.getRegistrationType(), BAD_REQUEST);

        verifyingPasswordsByAccountStatus(password, activeMember);

        activeMember.setLastLoginIP(ipAddress);

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(activeMember.getChangeableId());

        RefreshToken refreshToken = createNewRefreshToken(activeMember, jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill());

        eventPublisher.publishEvent(new MemberEventDto.Login(activeMember.getId(), "이메일 로그인", ipAddress, LocalDateTime.now()));

        return createLoginDto(activeMember, jwtTokenDto, refreshToken);

    }

    private Member findActiveMemberByEmail(String email) {
        List<Member> members = memberRepository.findAllByEmail(email);
        log.info("is true:{}",members.isEmpty());

        List<Member> activeMembers = findActiveMembers(members);

        emailDuplicationMemberVerification(activeMembers);

        return findActiveMember(activeMembers);
    }

    private void verifyingPasswordsByAccountStatus(String password, Member activeMember) {
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
    }

    private void registrationTypeVerification(RegistrationType targetType, RegistrationType expectedType) {
        if (targetType != expectedType) {
            throw new CustomRuntimeException(ALREADY_REGISTERED_BY_OAUTH2);
        }
    }

    private Member findActiveMember(List<Member> activeMembers) {
        return activeMembers.stream()
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeException(THIS_IS_NOT_REGISTERED_AS_MEMBER));
    }

    private void emailDuplicationMemberVerification(List<Member> activeMembers) {
        if (activeMembers.size() > 1) {
            Object array = activeMembers.stream().map(Member::getId).toArray();
            log.error("Duplicate Email members Occurrence ids: {}", array);
            throw new CustomRuntimeException(FOUND_ACTIVE_MEMBERS_WITH_DUPLICATE_EMAILS);
        }
    }

    private List<Member> findActiveMembers(List<Member> members) {
        return members.stream()
                .filter(member -> member.getAccountStatus() != AccountStatus.WITHDRAWN)
                .collect(Collectors.toList());
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


    @Override
    @Transactional
    public LoginResponse.LoginDto disposableAuthenticationCodeLoginProcessing(String code, String ipAddress) {
        log.info("Login By Disposable AuthenticationCode");

        Optional<DisposableAuthenticationCode> optionalAuthenticationCode = disposalAuthenticationRepository.findByCode(code);

        if (optionalAuthenticationCode.isEmpty()) {
            throw new CustomRuntimeException(WRONG_ONE_TIME_CODE);
        }

        DisposableAuthenticationCode authenticationCode = optionalAuthenticationCode.get();
        final Long memberId = authenticationCode.getMember().getId();

        Member member = findMemberByIdOrElseThrowRuntimeException(memberId, CAN_NOT_FIND_MEMBER_BY_DISPOSABLE_AUTHENTICATION_CODE);
        member.setLastLoginIP(ipAddress);

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());

        disposalAuthenticationRepository.deleteById(authenticationCode.getId());

        eventPublisher.publishEvent(new MemberEventDto.Login(member.getId(), "1회용 인증 코드 로그인", ipAddress, LocalDateTime.now()));

        return createLoginDto(member, jwtTokenDto, createNewRefreshToken(member, jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill()));

    }

    private Member findMemberByIdOrElseThrowRuntimeException(Long memberId, ApiResponseStatus status) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(status));
    }

    @Override
    @Transactional
    public LoginResponse.LogoutResult logoutProcessing(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout Processing");
        String accessToken = accessTokenResolver(request);

        if (accessToken == null) {
            throw new CustomRuntimeException(ApiResponseStatus.ACCESS_TOKEN_IS_EMPTY);
        }

        verifyingAccessToken(accessToken);

        String changeAbleId = jwtUtils.extractChangeableIdInToken(accessToken);

        redisRefreshTokenRepository
                .findById(changeAbleId)
                .ifPresent(redisRefreshTokenRepository::delete);

        Member member = findMemberByChangeAbleId(changeAbleId);
        member.replaceChangeableId();

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
                throw new CustomRuntimeException(ACCESS_TOKEN_IS_DENIED);
            case EXPIRED:
                throw new CustomRuntimeException(ACCESS_TOKEN_IS_EXPIRED);
            case ERROR:
                throw new CustomRuntimeException(ACCESS_TOKEN_IS_ERROR);
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

        Member member = findMemberByChangeAbleId(changeableId);
        member.replaceChangeableId();

        redisRefreshTokenRepository.deleteById(refresh.getChangeableId());

        JwtTokenDto jwtTokenDto = jwtUtils.generateJwtTokens(member.getChangeableId());

        RefreshToken savedRefreshToken = createNewRefreshToken(member, jwtTokenDto.getRefreshToken(), jwtTokenDto.getRefreshTokenExpirationMill());

        SecurityContextHolder.clearContext();

        return createLoginDto(member, jwtTokenDto, savedRefreshToken);
    }

    private void validateRefreshToken(String refreshToken) {
        JwtStatus refreshStatus = jwtUtils.validateToken(refreshToken);

        if (refreshStatus == JwtStatus.DENIED || refreshStatus == JwtStatus.ERROR) {
            throw new CustomRuntimeException(REFRESH_TOKEN_IS_DENIED);

        } else if (refreshStatus == JwtStatus.EXPIRED) {
            throw new CustomRuntimeException(REFRESH_TOKEN_IS_EXPIRED);
        }
    }

    private void validateAccessToken(String accessToken) {
        JwtStatus accessStatus = jwtUtils.validateToken(accessToken);
        if (accessStatus == JwtStatus.DENIED || accessStatus == JwtStatus.ERROR) {
            throw new CustomRuntimeException(ApiResponseStatus.ACCESS_TOKEN_IS_DENIED);
        }
    }

    /**
     * 리프레시 토큰을 찾지 못할 경우 로그아웃된 계정으로 간주
     */
    private RefreshToken findRefreshTokenByChangeableToken(String changeableToken) {
        return redisRefreshTokenRepository.findById(changeableToken)
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.ALREADY_LOGOUT));
    }


    private RefreshToken createNewRefreshToken(Member member, String refreshToken, long expiration) {
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
                .findMemberByChangeableIdAndDateIndex(changeableId, Member.getDateIndexInChangeableId(changeableId))
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public String getDisposableAuthenticationCode(Long memberId) {
        log.info("Get Disposable Authentication Code");
        Member member = findMemberById(memberId);

        DisposableAuthenticationCode disposableAuthenticationCode = DisposableAuthenticationCode.createDisposableAuthenticationCode(member);

        return disposalAuthenticationRepository.save(disposableAuthenticationCode).getCode();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
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

    private void verifiyingRequestedRefreshToken(String StoredRefreshToken, String refreshToken) {
        if (!StoredRefreshToken.equals(refreshToken)) {
            throw new CustomRuntimeException(REFRESH_TOKEN_IS_DENIED);
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
