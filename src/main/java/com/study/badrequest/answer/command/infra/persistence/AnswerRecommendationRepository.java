package com.study.badrequest.answer.command.infra.persistence;

import com.study.badrequest.answer.command.domain.AnswerRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRecommendationRepository extends JpaRepository<AnswerRecommendation,Long>, com.study.badrequest.answer.command.domain.AnswerRecommendationRepository {
}
