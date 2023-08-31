package com.study.badrequest.question.command.infra.persistence.writer;

import com.study.badrequest.question.command.infra.persistence.writer.WriterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WriterJpaRepository extends JpaRepository<WriterEntity,Long>{
    Optional<WriterEntity> findByMemberId(Long memberId);
}
