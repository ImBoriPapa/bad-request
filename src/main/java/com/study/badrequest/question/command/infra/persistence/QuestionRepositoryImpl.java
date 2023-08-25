package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final QuestionJpaRepository questionJpaRepository;
    private final EntityManager em;

    @Override
    public Question save(Question question) {
        return questionJpaRepository.save(QuestionEntity.formModel(question)).toModel();
    }

    @Override
    public Optional<Question> findById(Long questionId) {
        return Optional.empty();
    }

}
