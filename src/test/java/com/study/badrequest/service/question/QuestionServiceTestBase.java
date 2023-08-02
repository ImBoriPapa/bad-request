package com.study.badrequest.service.question;

import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.application.QuestionServiceImpl;
import com.study.badrequest.question.command.domain.QuestionRepository;
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
