package com.study.badrequest.question.command.infra.persistence;


import com.study.badrequest.question.command.domain.QuestionTag;
import com.study.badrequest.question.command.domain.QuestionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QuestionTagRepositoryImpl implements QuestionTagRepository {
    private final QuestionTagJpaRepository questionTagJpaRepository;

    @Override
    public QuestionTag save(QuestionTag questionTag) {
        return questionTagJpaRepository.save(QuestionTagEntity.fromModel(questionTag)).toModel();
    }

    @Override
    public Optional<QuestionTag> findById(Long id) {
        return questionTagJpaRepository.findById(id).map(QuestionTagEntity::toModel);
    }

    @Override
    public void delete(QuestionTag questionTag) {
        questionTagJpaRepository.delete(QuestionTagEntity.fromModel(questionTag));
    }

    @Override
    public List<QuestionTag> saveAllQuestionTag(List<QuestionTag> questionTags) {
        return questionTagJpaRepository
                .saveAll(questionTags.stream()
                        .map(QuestionTagEntity::fromModel)
                        .collect(Collectors.toList()))
                .stream()
                .map(QuestionTagEntity::toModel)
                .collect(Collectors.toList());
    }
}
