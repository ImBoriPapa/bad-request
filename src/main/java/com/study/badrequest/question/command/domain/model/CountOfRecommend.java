package com.study.badrequest.question.command.domain.model;

import lombok.Getter;

@Getter
public class CountOfRecommend {
    private final Long id;
    private final Long count;
    public CountOfRecommend(Long id, Long count) {
        this.id = id;
        this.count = count;
    }

    public static CountOfRecommend createNewRecommendCount(Long count) {
        return new CountOfRecommend(null, count);
    }

    public static CountOfRecommend initialize(Long id, Long count){
        return new CountOfRecommend(id, count);
    }
}
