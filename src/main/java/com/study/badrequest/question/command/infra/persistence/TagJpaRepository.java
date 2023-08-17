package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.Tag;
import com.study.badrequest.question.command.domain.TagRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, Long>, TagRepository {


}
