package com.study.badrequest.service.question;

import com.study.badrequest.domain.question.Question;

import java.util.List;

public interface QuestionTagService {
    void createQuestionTag(Long questionId,List<String> tags);
}
