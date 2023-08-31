package com.study.badrequest.question.command.infra.persistence.question;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewCountOfQuestionJpaRepository extends JpaRepository<CountOfViewEntity,Long> {
    Optional<CountOfViewEntity> findByCount(Long count);
}
