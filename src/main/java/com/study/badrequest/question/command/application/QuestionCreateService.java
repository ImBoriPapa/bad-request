package com.study.badrequest.question.command.application;

import com.study.badrequest.question.command.application.dto.CreateQuestionForm;

public interface QuestionCreateService {
    Long createQuestion(CreateQuestionForm form);
}
