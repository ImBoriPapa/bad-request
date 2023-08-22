package com.study.badrequest.recommandation.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<QuestionRecommendation,Long> {
    Optional<QuestionRecommendation> findByMemberIdAndQuestionId(Long memberId, Long questionId);

    boolean existsByMemberIdAndQuestionId(Long memberId, Long questionId);
}
