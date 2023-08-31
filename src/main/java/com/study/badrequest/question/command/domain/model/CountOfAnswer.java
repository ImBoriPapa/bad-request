package com.study.badrequest.question.command.domain.model;

import lombok.Getter;

@Getter
public class CountOfAnswer {
    private final Long id;
    private final Long count;

    public CountOfAnswer(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfAnswer createNewAnswerCount(Long count) {
        return new CountOfAnswer(null, count);
    }
}
