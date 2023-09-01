package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfRecommend;
import com.study.badrequest.question.command.domain.repository.CountOfRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CountOfRecommendRepositoryImpl implements CountOfRecommendRepository {

    private final CountOfRecommendJpaRepository countOfRecommendJpaRepository;

    @Override
    public CountOfRecommend save(CountOfRecommend newRecommendCount) {
        return countOfRecommendJpaRepository.save(CountOfRecommendEntity.fromModel(newRecommendCount)).toModel();
    }

    @Override
    public Optional<CountOfRecommend> findByCount(Long count) {
        return countOfRecommendJpaRepository.findByCount(count).map(CountOfRecommendEntity::toModel);
    }
}
