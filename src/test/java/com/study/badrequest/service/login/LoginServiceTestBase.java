package com.study.badrequest.service.login;

import com.study.badrequest.member.command.application.LoginServiceImpl;
import com.study.badrequest.repository.login.DisposalAuthenticationRepository;
import com.study.badrequest.repository.login.RedisRefreshTokenRepository;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.member.command.domain.TemporaryPasswordRepository;
import com.study.badrequest.utils.jwt.JwtUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class LoginServiceTestBase {

    @InjectMocks
    protected LoginServiceImpl loginService;
    @Mock
    protected MemberRepository memberRepository;
    @Mock
    protected RedisRefreshTokenRepository redisRefreshTokenRepository;
    @Mock
    protected DisposalAuthenticationRepository disposalAuthenticationRepository;
    @Mock
    protected   TemporaryPasswordRepository temporaryPasswordRepository;
    @Mock
    protected JwtUtils jwtUtils;
    @Spy
    protected PasswordEncoder passwordEncoder;
    @Mock
    protected ApplicationEventPublisher eventPublisher;


}
