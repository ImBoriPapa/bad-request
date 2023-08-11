package com.study.badrequest.image.command.infra.persistence;

import com.study.badrequest.image.command.domain.QuestionImage;
import com.study.badrequest.image.command.domain.QuestionImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaImageRepository extends JpaRepository<QuestionImage,Long>, QuestionImageRepository {
}
