package com.study.badrequest.question.command.domain.repository;

import com.study.badrequest.question.command.domain.model.CountOfAnswer;

import java.util.Optional;

public interface CountOfAnswerRepository {
    Optional<CountOfAnswer> findByCount(Long count);

    CountOfAnswer save(CountOfAnswer newAnswerCount);
}
