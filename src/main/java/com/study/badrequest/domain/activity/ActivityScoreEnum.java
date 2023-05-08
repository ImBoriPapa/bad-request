package com.study.badrequest.domain.activity;

import lombok.Getter;

@Getter
public enum ActivityScoreEnum {
    WRITE_QUESTION(10),
    WRITE_ANSWER(20),
    ADD_COMMENT(10),
    SCRAP_QUESTION(10);
    private final int score;
    ActivityScoreEnum(Integer score) {
        this.score = score;
    }
}
