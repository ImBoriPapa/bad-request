package com.study.badrequest.question.command.domain;

import com.study.badrequest.question.command.infra.persistence.QuestionEntity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
public final class QuestionTag {

    private final Long id;

    private final Question question;
    private final Tag tag;

    public QuestionTag(Long id, Question question, Tag tag) {
        this.id = id;
        this.question = question;
        this.tag = tag;
    }

    public static QuestionTag createQuestionTag(Question question, Tag tag) {
        return new QuestionTag(null, question, tag);
    }

    public static QuestionTag initialize(Long id, Question question, Tag tag) {
        return new QuestionTag(id, question, tag);
    }

}
