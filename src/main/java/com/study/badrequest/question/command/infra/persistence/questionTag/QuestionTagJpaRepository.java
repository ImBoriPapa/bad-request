package com.study.badrequest.question.command.infra.persistence.questionTag;

import com.study.badrequest.question.command.infra.persistence.questionTag.QuestionTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionTagJpaRepository extends JpaRepository<QuestionTagEntity,Long> {
}
