package com.study.badrequest.question.command.infra.persistence.question;

import com.study.badrequest.question.command.domain.model.Question;
import com.study.badrequest.question.command.domain.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

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
