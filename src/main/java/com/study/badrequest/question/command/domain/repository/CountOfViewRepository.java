package com.study.badrequest.question.command.domain.repository;


import com.study.badrequest.question.command.domain.model.CountOfView;

import java.util.Optional;

public interface CountOfViewRepository {
    CountOfView save(CountOfView countOfView);

    Optional<CountOfView> findByCount(Long count);
}
