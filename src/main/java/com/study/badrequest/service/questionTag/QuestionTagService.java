package com.study.badrequest.service.questionTag;

import com.study.badrequest.domain.question.Question;

import java.util.List;

public interface QuestionTagService {
    void createQuestionTag(List<String> tags, Question question);
}
