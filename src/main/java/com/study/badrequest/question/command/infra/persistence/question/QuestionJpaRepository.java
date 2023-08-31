package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.infra.persistence.question.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity,Long> {
}
