package com.study.badrequest.hashtag.command.domain;


import java.util.List;
import java.util.Optional;

public interface TagRepository {

    Tag save(Tag tag);

    Optional<Tag> findById(Long id);

    List<Tag> findAllById(Iterable<Long> ids);

    Optional<Tag> findByName(String tag);

    List<Tag> findAllByNameIn(Iterable<String> tags);
}
