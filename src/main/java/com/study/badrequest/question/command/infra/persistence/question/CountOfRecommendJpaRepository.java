package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.CountOfRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountOfRecommendJpaRepository extends JpaRepository<CountOfRecommendEntity,Long> {
    Optional<CountOfRecommendEntity> findByCount(Long count);
}
