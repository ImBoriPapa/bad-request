package com.study.badrequest.service.question;

import com.study.badrequest.question.command.application.QuestionTagServiceImpl;
import com.study.badrequest.hashtag.command.domain.HashTagRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import com.study.badrequest.question.command.domain.QuestionTagRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public abstract class QuestionTagServiceTestBase {

    @InjectMocks
    protected QuestionTagServiceImpl questionTagService;
    @Mock
    protected QuestionTagRepository questionTagRepository;
    @Mock
    protected HashTagRepository hashTagRepository;

    @Mock
    protected QuestionRepository questionRepository;
}
