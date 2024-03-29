package com.study.badrequest.question.command.domain.repository;


import com.study.badrequest.question.command.domain.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Tag save(Tag tag);

    Optional<Tag> findById(Long id);

    Optional<Tag> findByName(String name);

    List<Tag> findAllByNameIn(List<Long> tags);
}
