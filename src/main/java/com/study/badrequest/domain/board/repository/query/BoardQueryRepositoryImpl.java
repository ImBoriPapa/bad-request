package com.study.badrequest.domain.board.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.domain.board.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.domain.board.entity.QBoard.board;
import static com.study.badrequest.domain.board.entity.QBoardImage.*;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Board findBoardDetail(Long id) {
        return jpaQueryFactory
                .select(board)
                .from(board,boardImage)
                .where(board.id.eq(boardImage.board.id))
                .fetchOne();
    }

    @Override
    public BoardListDto findBoardList(int size, Long lastIndex, String keyword, Category category, Topic topic) {
        List<BoardListResult> results = jpaQueryFactory
                .select(Projections.fields(BoardListResult.class,
                        board.id.as("boardId"),
                        board.member.nickname.as("nickname"),
                        board.title.as("title"),
                        board.likeCount.as("likeCount"),
                        board.category.as("category"),
                        board.topic.as("topic"),
                        board.createdAt.as("createdAt")
                ))
                .from(board)
                .where(cursor(lastIndex),
                        eqCategory(category),
                        eqKeyword(keyword),
                        eqTopic(topic)
                ).orderBy(board.id.desc())
                .limit(size + 1)
                .fetch();


        boolean hasNext = results.size() > size;

        if (hasNext) {
            results.remove(size);
        }

        return new BoardListDto(size, hasNext, results);
    }

    private BooleanExpression cursor(Long boardId) {
        return boardId <= 0 ? null : QBoard.board.id.lt(boardId);
    }

    private BooleanExpression eqCategory(Category category) {
        return category != null ? board.category.eq(category) : null;
    }

    private BooleanExpression eqKeyword(String keyword) {
        return keyword != null ? board.title.contains(keyword) : null;
    }

    private BooleanExpression eqTopic(Topic topic) {
        return topic != null ? board.topic.eq(topic) : null;
    }
}
