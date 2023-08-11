package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.QuestionTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionTagCustomRepositoryImpl implements QuestionTagCustomRepository {

    private final EntityManager entityManager;

    public List<QuestionTag> saveAllQuestionTags(Iterable<QuestionTag> questionTags) {
        return Collections.emptyList();
    }

}
