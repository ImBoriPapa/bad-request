package com.study.badrequest.repository.questionComment.query;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.domain.questionComment.QuestionComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


import static com.study.badrequest.domain.questionComment.QQuestionComment.questionComment;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuestionCommentQueryRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public void findQuestionCommentsByQuestion(Long questionId) {
        List<QuestionComment> questionComments = jpaQueryFactory
                .select(questionComment)
                .where(questionComment.question.id.eq(questionId))
                .fetch();
    }
}
