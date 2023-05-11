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
    public LoginResponse.LoginDto emailLoginProcessing(String email, String password, String ipAddress) {
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
    public LoginResponse.LoginDto loginByTemporaryAuthenticationCode(String code, String ipAddress) {

        Member member = memberRepository.findByOneTimeAuthenticationCode(code)
                .orElseThrow(() -> new MemberExceptionBasic(WRONG_ONE_TIME_CODE));

        if (!member.getOneTimeAuthenticationCode().equals(code)) {
            SecurityContextHolder.clearContext();
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        if (member.getAbleUseOneTimeAuthenticationCode()) {
            SecurityContextHolder.clearContext();
            throw new IllegalArgumentException("만료된 인증 코드입니다. 사용하실 수 없습니다.");
        }

        member.useOneTimeAuthenticationCode();

        TokenDto tokenDto = jwtUtils.generateJwtTokens(member.getUsername());
        //RefreshToken 저장
        RefreshToken refreshToken = storeRefreshToken(member.getUsername(), member.getId(), member.getAuthority(), tokenDto);

        member.setLastLoginIP(ipAddress);

        eventPublisher.publishEvent(new MemberEventDto.Login(member, "일반 로그인", LocalDateTime.now()));
        //응답 객체 생성
        return LoginResponse.LoginDto.builder()
                .id(member.getId())
                .accessToken(tokenDto.getAccessToken())
                .refreshCookie(CookieFactory.createRefreshTokenCookie(refreshToken.getToken(), refreshToken.getExpiration()))
                .loggedIn(tokenDto.getAccessTokenExpiredAt())
                .build();
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
                .refreshCookie(CookieFactory.createRefreshTokenCookie(refresh.getToken(), refresh.getExpiration()))
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

    private RefreshToken findRefreshByAccessToken(String accessToken) {
        return redisRefreshTokenRepository.findById(jwtUtils.getUsernameInToken(accessToken))
                .orElseThrow(() -> new JwtAuthenticationExceptionBasic(ApiResponseStatus.ALREADY_LOGOUT));
    }


    private void compareRequestedPasswordWithStored(String requestedPassword, String storedPassword) {
        if (!passwordEncoder.matches(requestedPassword, storedPassword)) {
            throw new BasicCustomException(ApiResponseStatus.LOGIN_FAIL);
        }
    }

    /**
     * 회원 식별자, username, 권한 정보만 조회
     * 조회시 권한정보로 인덱싱
     */
    private MemberSimpleInformation findMemberInformation(String username, Authority authority) {
        log.info("findMemberInformation {}, {}", username, authority);

        return memberRepository
                .findByUsernameAndAuthority(username, authority)
                .orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));
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

    @Transactional
    public void replaceRefreshToken(RefreshToken refresh, TokenDto tokenDto) {
        refresh.replaceToken(tokenDto.getRefreshToken(), tokenDto.getRefreshTokenExpirationMill());
        redisRefreshTokenRepository.save(refresh);
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
