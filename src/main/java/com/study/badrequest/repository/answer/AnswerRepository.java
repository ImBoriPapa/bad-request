package com.study.badrequest.repository.answer;

import com.study.badrequest.domain.question.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
}
