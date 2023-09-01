package com.study.badrequest.question.command.infra.persistence.question;


import com.study.badrequest.question.command.domain.model.CountOfUnRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountOfUnRecommendJpaRepository extends JpaRepository<CountOfUnRecommendEntity,Long> {
    Optional<CountOfUnRecommendEntity> findByCount(Long count);
}
