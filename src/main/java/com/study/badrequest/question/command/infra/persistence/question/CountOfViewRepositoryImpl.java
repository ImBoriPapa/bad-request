package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfView;
import com.study.badrequest.question.command.domain.repository.CountOfViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CountOfViewRepositoryImpl implements CountOfViewRepository {
    private final ViewCountOfQuestionJpaRepository viewCountOfQuestionJpaRepository;

    @Override
    public CountOfView save(CountOfView countOfView) {
        return viewCountOfQuestionJpaRepository.save(CountOfViewEntity.fromModel(countOfView)).toModel();
    }

    @Override
    public Optional<CountOfView> findByCount(Long count) {
        return viewCountOfQuestionJpaRepository.findByCount(count).map(CountOfViewEntity::toModel);
    }
}
