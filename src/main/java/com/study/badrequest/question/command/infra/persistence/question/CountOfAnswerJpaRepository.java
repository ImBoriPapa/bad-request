package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountOfAnswerJpaRepository extends JpaRepository<CountOfAnswerEntity,Long> {
    Optional<CountOfAnswerEntity> findByCount(Long count);
}
