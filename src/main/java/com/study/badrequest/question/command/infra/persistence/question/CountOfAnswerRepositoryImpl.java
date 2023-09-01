package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfAnswer;
import com.study.badrequest.question.command.domain.repository.CountOfAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CountOfAnswerRepositoryImpl implements CountOfAnswerRepository {

    private final CountOfAnswerJpaRepository countOfAnswerJpaRepository;

    @Override
    public CountOfAnswer save(CountOfAnswer newAnswerCount) {
        return countOfAnswerJpaRepository.save(CountOfAnswerEntity.fromModel(newAnswerCount)).toModel();
    }

    @Override
    public Optional<CountOfAnswer> findByCount(Long count) {
        return countOfAnswerJpaRepository.findByCount(count).map(CountOfAnswerEntity::toModel);
    }
}
