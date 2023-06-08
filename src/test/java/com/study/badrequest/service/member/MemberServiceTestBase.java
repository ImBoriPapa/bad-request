package com.study.badrequest.service.member;

import com.study.badrequest.repository.member.EmailAuthenticationCodeRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.member.TemporaryPasswordRepository;
import com.study.badrequest.utils.image.ImageUploader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class MemberServiceTestBase {

    @InjectMocks
    protected MemberServiceImpl memberService;
    @Mock
    protected PasswordEncoder passwordEncoder;
    @Mock
    protected MemberRepository memberRepository;
    @Mock
    protected EmailAuthenticationCodeRepository emailAuthenticationCodeRepository;
    @Mock
    protected ImageUploader imageUploader;
    @Mock
    protected TemporaryPasswordRepository temporaryPasswordRepository;
    @Mock
    protected ApplicationEventPublisher eventPublisher;

}
