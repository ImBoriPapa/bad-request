package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.Writer;
import com.study.badrequest.question.command.domain.WriterRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WriterJpaRepository extends JpaRepository<WriterEntity,Long>{
    Optional<WriterEntity> findByMemberId(Long memberId);
}
