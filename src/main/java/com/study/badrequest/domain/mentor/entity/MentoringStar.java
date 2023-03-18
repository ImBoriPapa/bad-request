package com.study.badrequest.domain.mentor.entity;

import lombok.Getter;

@Getter
public enum MentoringStar {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private Integer score;

    MentoringStar(Integer score) {
        this.score = score;
    }
}
