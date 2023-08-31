package com.study.badrequest.question.command.domain.repository;

import com.study.badrequest.question.command.domain.model.CountOfRecommend;

import java.util.Optional;

public interface CountOfRecommendRepository {
    Optional<CountOfRecommend> findByCount(Long count);

    CountOfRecommend save(CountOfRecommend newRecommendCount);
}
