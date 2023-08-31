package com.study.badrequest.question.command.infra.persistence.tag;

import com.study.badrequest.question.command.infra.persistence.tag.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagJpaRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);

    List<TagEntity> findByIdIn(Collection<Long> id);
}
