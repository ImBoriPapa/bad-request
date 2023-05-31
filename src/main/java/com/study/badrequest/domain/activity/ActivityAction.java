package com.study.badrequest.domain.activity;

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
