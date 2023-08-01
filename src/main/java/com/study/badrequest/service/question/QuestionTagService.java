package com.study.badrequest.service.question;

import com.study.badrequest.dto.question.QuestionTagResponse;

import java.util.List;

public interface QuestionTagService {
    QuestionTagResponse.Create createQuestionTagProcessing(Long questionId, List<String> tags);

    QuestionTagResponse.Add addQuestionTagProcessing(Long questionId, String tagName);
}
