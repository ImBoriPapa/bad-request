package com.study.badrequest.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.study.badrequest.domain.comment.entity.QSubComment.*;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TestRepository {

    private final EntityManager em;

    private final JPAQueryFactory jpaQueryFactory;

    public Long subCommentCount(Long boardId) {
        return jpaQueryFactory
                .select(subComment.count())
                .from(subComment)
                .where(subComment.board.id.eq(boardId))
                .fetchOne();
    }
}
