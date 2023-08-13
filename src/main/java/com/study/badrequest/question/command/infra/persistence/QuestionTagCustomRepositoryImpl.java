package com.study.badrequest.question.command.infra.persistence;

import com.study.badrequest.question.command.domain.QuestionTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class QuestionTagCustomRepositoryImpl implements QuestionTagCustomRepository {

    private final EntityManager entityManager;

    public List<QuestionTag> saveAllQuestionTag(Iterable<QuestionTag> questionTags) {
        ArrayList<QuestionTag> persistedTags = new ArrayList<>();

        for (QuestionTag questionTag : questionTags) {
            entityManager.persist(questionTag);
            persistedTags.add(questionTag);
        }

        return persistedTags;
    }

}
