package com.study.badrequest.question.command.application;

import com.study.badrequest.question.query.interfaces.QuestionTagResponse;

import java.util.List;

public interface QuestionTagService {
    QuestionTagResponse.Create createQuestionTagProcessing(Long questionId, List<String> tags);

    QuestionTagResponse.Add addQuestionTagProcessing(Long questionId, String tagName);
}
