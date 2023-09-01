package com.study.badrequest.question.command.application;

import com.study.badrequest.question.command.application.dto.CreateQuestionRequest;

public interface QuestionCreateService {

    Long createQuestion(CreateQuestionRequest createQuestionRequest);
}
