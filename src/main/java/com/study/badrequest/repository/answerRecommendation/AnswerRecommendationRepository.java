package com.study.badrequest.repository.answerRecommendation;

import com.study.badrequest.answer.command.domain.AnswerRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRecommendationRepository extends JpaRepository<AnswerRecommendation,Long> {
}
