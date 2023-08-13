package com.study.badrequest.question.command.domain;

import java.util.Optional;

public interface WriterRepository {
    Writer save(Writer writer);

    Optional<Writer> findById(Long id);
}
