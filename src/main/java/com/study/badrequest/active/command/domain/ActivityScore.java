package com.study.badrequest.active.command.domain;

import lombok.Getter;

@Getter
public enum ActivityScore {
    WRITE_QUESTION(10),
    WRITE_ANSWER(20),
    ADD_COMMENT(10),
    SCRAP_QUESTION(10);
    private final int score;
    ActivityScore(Integer score) {
        this.score = score;
    }
}
