package com.study.badrequest.service.question;

import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

public abstract class QuestionServiceTestBase {
    @InjectMocks
    protected QuestionServiceImpl questionService;
    @Mock
    protected MemberRepository memberRepository;
    @Mock
    protected QuestionRepository questionRepository;
    @Mock
    protected ApplicationEventPublisher eventPublisher;
}
