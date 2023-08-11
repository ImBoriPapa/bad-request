package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.QuestionTag;

import java.util.List;


public interface QuestionTagCustomRepository{
    List<QuestionTag> saveAllQuestionTags(Iterable<QuestionTag> questionTags);
}
