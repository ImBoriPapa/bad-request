package com.study.badrequest.active.command.domain;

import lombok.Getter;

@Getter
public enum ActivityAction {
    QUESTION(10),
    ANSWER(20),
    COMMENT(10),
    SCRAP(10);
    private final int score;
    ActivityAction(int score) {
        this.score = score;
    }
}
