package com.study.badrequest.service.member;

import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.utils.image.ImageUploader;
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
