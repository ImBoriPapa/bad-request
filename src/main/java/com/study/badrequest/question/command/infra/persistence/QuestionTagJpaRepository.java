package com.study.badrequest.question.command.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionTagJpaRepository extends JpaRepository<QuestionTagEntity,Long> {
}
