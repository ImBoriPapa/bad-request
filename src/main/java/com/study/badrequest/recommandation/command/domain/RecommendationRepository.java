package com.study.badrequest.recommandation.command.domain;

import com.study.badrequest.recommandation.command.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation,Long> {
    Optional<Recommendation> findByMemberIdAndQuestionId(Long memberId,Long questionId);

    boolean existsByMemberIdAndQuestionId(Long memberId, Long questionId);
}
