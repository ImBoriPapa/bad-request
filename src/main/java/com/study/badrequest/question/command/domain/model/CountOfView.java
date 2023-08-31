package com.study.badrequest.question.command.domain.model;

import lombok.Getter;

@Getter
public class CountOfView {
    private final Long id;
    private final Long count;

    private CountOfView(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfView createNewViewCountOfQuestion(Long count) {
        return new CountOfView(null, count);
    }

    public static CountOfView initialize(Long id, Long count) {
        return new CountOfView(id, count);
    }
}
