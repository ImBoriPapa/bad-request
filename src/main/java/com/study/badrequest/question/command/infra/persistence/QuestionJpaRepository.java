package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<Question,Long>, QuestionRepository {
}
