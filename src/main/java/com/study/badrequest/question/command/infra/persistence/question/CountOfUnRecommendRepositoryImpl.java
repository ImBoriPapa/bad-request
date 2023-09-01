package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfUnRecommend;
import com.study.badrequest.question.command.domain.repository.CountOfUnRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CountOfUnRecommendRepositoryImpl implements CountOfUnRecommendRepository {

    private final CountOfUnRecommendJpaRepository countOfUnRecommendJpaRepository;

    @Override
    public CountOfUnRecommend save(CountOfUnRecommend newUnRecommendCount) {
        return countOfUnRecommendJpaRepository.save(CountOfUnRecommendEntity.fromModel(newUnRecommendCount)).toModel();
    }

    @Override
    public Optional<CountOfUnRecommend> findByCount(Long count) {
        return countOfUnRecommendJpaRepository.findByCount(count).map(CountOfUnRecommendEntity::toModel);
    }
}
