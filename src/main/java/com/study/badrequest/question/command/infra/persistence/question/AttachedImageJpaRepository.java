package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.AttachedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachedImageJpaRepository extends JpaRepository<AttachedImageEntity,Long> {
    List<AttachedImageEntity> findAllByIdIn(List<Long> ids);
}
