package com.study.badrequest.service.member;

import com.study.badrequest.member.command.application.MemberProfileServiceImpl;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.image.command.infra.ImageUploader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

public abstract class MemberProfileServiceTestBase {
    @InjectMocks
    protected MemberProfileServiceImpl memberProfileService;

    @Mock
    protected MemberRepository memberRepository;
    @Mock
    protected ImageUploader imageUploader;

    @Mock
    protected ApplicationEventPublisher eventPublisher;
}
