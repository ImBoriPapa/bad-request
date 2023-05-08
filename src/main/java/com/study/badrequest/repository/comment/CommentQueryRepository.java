package com.study.badrequest.repository.comment;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.repository.comment.query.CommentDto;
import com.study.badrequest.repository.comment.query.CommentListDto;
import com.study.badrequest.repository.comment.query.SubCommentDto;
import com.study.badrequest.repository.comment.query.SubCommentListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalLong;

import static com.study.badrequest.domain.comment.QComment.comment;

import static com.study.badrequest.domain.comment.QSubComment.subComment;
import static com.study.badrequest.domain.member.QMember.member;



@Repository
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시판에 댓글모두 가져오기
     */
    public CommentListDto findAllCommentByBoardId(Long boardId, CommentSearchCondition condition) {

        int limitSize = setLimitSize(condition.getSize());
        long lastIndex = setLastIndex(condition.getLastIndex());
        int resultSize;
        boolean hasNext;

        List<CommentDto> results = jpaQueryFactory
                .select(Projections.fields(CommentDto.class,
                        comment.id.as("commentId"),
                        comment.board.id.as("boardId"),
                        comment.member.id.as("memberId"),
                        comment.member.memberProfile.profileImage.imageLocation.as("profileImage"),
                        comment.member.memberProfile.as("nickname"),
                        comment.text.as("text"),
                        comment.likeCount.as("likeCount"),
                        comment.subCommentCount.as("subCommentCount"),
                        comment.createdAt.as("createdAt"),
                        comment.updatedAt.as("updatedAt")
                ))
                .from(comment)
                .leftJoin(comment.member, member)
                .where(comment.board.id.eq(boardId),
                        commentCursor(lastIndex)
                )
                .orderBy(comment.id.asc())
                .limit(limitSize + 1)
                .fetch();

        resultSize = results.size();

        hasNext = resultSize > limitSize;

        if (hasNext) {
            results.remove(limitSize);
            --resultSize;
        }

        OptionalLong resultLastIndex = results.stream()
                .mapToLong(CommentDto::getCommentId)
                .max();

        return CommentListDto
                .builder()
                .size(resultSize)
                .hasNext(hasNext)
                .lastIndex(resultLastIndex.orElse(0L))
                .results(results)
                .build();
    }


    /**
     * 댓글 아이디로 게시판에 대댓글 가져오기
     */
    public SubCommentListDto findAllSubCommentByCommentId(Long commentId, CommentSearchCondition condition) {

        int limitSize = setLimitSize(condition.getSize());
        long lastIndex = setLastIndex(condition.getLastIndex());
        int resultSize;
        boolean hasNext;

        List<SubCommentDto> results = jpaQueryFactory
                .select(Projections.fields(SubCommentDto.class,
                        subComment.id.as("subCommentId"),
                        subComment.comment.id.as("commentId"),
                        subComment.board.id.as("boardId"),
                        subComment.member.id.as("memberId"),
                        comment.member.memberProfile.profileImage.imageLocation.as("profileImage"),
                        comment.member.memberProfile.as("nickname"),
                        subComment.text.as("text"),
                        subComment.likeCount.as("likeCount"),
                        subComment.createdAt.as("createdAt"),
                        subComment.updatedAt.as("updatedAt")
                ))
                .from(subComment)
                .leftJoin(subComment.member, member)
                .where(subComment.comment.id.eq(commentId),
                        subCommentCursor(lastIndex)
                )
                .orderBy(subComment.id.asc())
                .limit(limitSize + 1)
                .fetch();

        resultSize = results.size();

        hasNext = resultSize > limitSize;

        if (hasNext) {
            results.remove(limitSize);
            --resultSize;
        }

        OptionalLong resultLastIndex = results.stream()
                .mapToLong(SubCommentDto::getSubCommentId)
                .max();

        return SubCommentListDto
                .builder()
                .size(resultSize)
                .hasNext(hasNext)
                .lastIndex(resultLastIndex.orElse(0L))
                .results(results)
                .build();
    }

    private long setLastIndex(Long lastIndex) {
        return lastIndex == null ? 0L : lastIndex;
    }

    private int setLimitSize(Integer size) {
        return size == null ? 5 : size;
    }

    private BooleanExpression commentCursor(Long lastIndex) {
        return lastIndex <= 0 ? null : comment.id.gt(lastIndex);
    }

    private BooleanExpression subCommentCursor(Long lastIndex) {
        return lastIndex <= 0 ? null : subComment.id.gt(lastIndex);
    }
}
