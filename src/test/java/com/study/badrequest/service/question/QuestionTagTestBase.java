package com.study.badrequest.service.question;

import com.study.badrequest.repository.hashTag.HashTagRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public abstract class QuestionTagTestBase {

    @InjectMocks
    protected QuestionTagServiceImpl questionTagService;
    @Mock
    protected QuestionTagRepository questionTagRepository;
    @Mock
    protected HashTagRepository hashTagRepository;

    @Mock
    protected QuestionRepository questionRepository;
}
