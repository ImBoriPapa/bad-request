package com.study.badrequest.question.command.domain.repository;

import com.study.badrequest.question.command.domain.model.Writer;

import java.util.Optional;

public interface WriterRepository {
    Writer save(Writer writer);

    Optional<Writer> findById(Long id);

    Optional<Writer> findByMemberId(Long memberId);
}
