package com.study.badrequest.question.command.domain.repository;

import com.study.badrequest.question.command.domain.model.CountOfUnRecommend;

import java.util.Optional;

public interface CountOfUnRecommendRepository {
    Optional<CountOfUnRecommend> findByCount(Long count);

    CountOfUnRecommend save(CountOfUnRecommend newUnRecommendCount);
}
