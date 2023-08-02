package com.study.badrequest.answer.command.domain;

import com.study.badrequest.answer.command.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
}
