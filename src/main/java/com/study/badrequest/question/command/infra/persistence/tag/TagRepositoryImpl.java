package com.study.badrequest.question.command.infra.persistence.tag;

import com.study.badrequest.question.command.domain.model.Tag;
import com.study.badrequest.question.command.domain.repository.TagRepository;
import com.study.badrequest.question.command.infra.persistence.tag.TagEntity;
import com.study.badrequest.question.command.infra.persistence.tag.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagJpaRepository tagJpaRepository;

    @Override
    public Tag save(Tag tag) {
        return tagJpaRepository.save(TagEntity.fromModel(tag)).toModel();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return tagJpaRepository.findById(id).map(TagEntity::toModel);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagJpaRepository.findByName(name).map(TagEntity::toModel);
    }

    @Override
    public List<Tag> findAllByNameIn(List<Long> tags) {
        return tagJpaRepository.findByIdIn(tags).stream().map(TagEntity::toModel).collect(Collectors.toList());
    }
}
