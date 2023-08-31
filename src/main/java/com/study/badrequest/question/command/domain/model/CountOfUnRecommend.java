package com.study.badrequest.question.command.domain.model;

import lombok.Getter;

@Getter
public class CountOfUnRecommend {

    private final Long id;

    private final Long count;

    public CountOfUnRecommend(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfUnRecommend createNewUnRecommendCount(Long count) {
        return new CountOfUnRecommend(null, count);
    }
}
