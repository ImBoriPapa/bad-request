package com.study.badrequest.domain.comment.repository;


import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.comment.repository.dto.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;


import static com.study.badrequest.domain.member.entity.QMember.member;
import static com.study.badrequest.domain.comment.entity.QComment.comment;
import static com.study.badrequest.domain.comment.entity.QSubComment.*;


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
                        comment.member.profileImage.fullPath.as("profileImage"),
                        comment.member.nickname.as("nickname"),
                        comment.text.as("text"),
                        comment.likeCount.as("likeCount"),
                        comment.subCommentCount.as("subCommentCount"),
                        comment.createdAt.as("createdAt"),
                        comment.updatedAt.as("updatedAt")
                ))
                .from(comment)
                .leftJoin(comment.member, member)
                .where(comment.board.id.eq(boardId),
                        cursor(lastIndex)
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
                        subComment.member.profileImage.fullPath.as("profileImage"),
                        subComment.member.nickname.as("nickname"),
                        subComment.text.as("text"),
                        subComment.likeCount.as("likeCount"),
                        subComment.createdAt.as("createdAt"),
                        subComment.updatedAt.as("updatedAt")
                ))
                .from(subComment)
                .leftJoin(subComment.member, member)
                .where(subComment.comment.id.eq(commentId),
                        cursor(lastIndex)
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
                .mapToLong(SubCommentDto::getCommentId)
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

    private BooleanExpression cursor(Long lastIndex) {
        return lastIndex <= 0 ? null : comment.id.gt(lastIndex);
    }

    /**
     * @deprecated Entity: Comment,SubComment 의 연관관계가 단방향 N : 1 변경 되어 사용 불가 정상 작동 안됨
     * Member Entity , SubComment Entity fetch 조인 후 한방 쿼리로 조회
     * 장점: 한번 쿼리
     * 단점 1:  1:N 관계로 Projection 불가
     * 단점 2:  Comment,SubComment 일대다 ,다대일 양방향 관계
     */
    @Deprecated
    public List<CommentDto> findCommentByBoardJustOneQuery(Long boardId) {
        log.info("=================[getComments QUERY START]=================");
        List<CommentDto> collect = jpaQueryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.member)
                .fetchJoin()
//                .leftJoin(comment.subCommentList)
//                .fetchJoin()
                .where(comment.board.id.eq(boardId))
                .distinct()
                .fetch()
                .stream()
                .map(r -> new CommentDto()) //임시 생성자 (작동 안됨)
//                .map(CommentDto::new)
                .collect(Collectors.toList());
        log.info("=================[getComments QUERY FINISH]=================");

        return collect;
    }


    /**
     * @deprecated Comment와 SubComment의 분리 예정
     * <p>
     * 게시판에 댓글,대댓글 모두 가져오기
     * Comment Entity Member Entity 조인, SubComment Entity Member Entity 조인 후 각각 조회 후 반환
     * 장점1 : Projections 으로 필요한 필드만 조회가능
     * 장점2 : Comment,SubComment 다대일 단방향 설계 가능
     * 단점: 쿼리가 두번 나감
     */
    @Deprecated
    public CommentListDto findAllCommentAndSubCommentByBoardId(Long boardId) {
        log.info("=================[findComments QUERY START]=================");
//        List<CommentDto> commentDtoList = findAllCommentByBoardId(boardId,1);

        List<SubCommentDto> subCommentDtoList = findAllSubCommentByBoardId(boardId);

//        commentDtoList.forEach(list -> addSubToComment(list, subCommentDtoList));
        log.info("=================[findComments QUERY FINISH]=================");

        return CommentListDto.builder()
//                .results(commentDtoList)
                .build();
    }

    /**
     * 게시판 아이디로 게시판에 대댓글 가져오기
     *
     * @deprecated 댓글과 대댓글 API 분리 후 Deprecate 예정
     */
    @Deprecated
    public List<SubCommentDto> findAllSubCommentByBoardId(Long boardId) {
        return jpaQueryFactory
                .select(Projections.fields(SubCommentDto.class,
                        subComment.id.as("subCommentId"),
                        subComment.comment.id.as("commentId"),
                        subComment.board.id.as("boardId"),
                        subComment.member.id.as("memberId"),
                        subComment.member.profileImage.fullPath.as("profileImage"),
                        subComment.member.nickname.as("nickname"),
                        subComment.text.as("text"),
                        subComment.likeCount.as("likeCount"),
                        subComment.createdAt.as("createdAt"),
                        subComment.updatedAt.as("updatedAt")
                ))
                .from(subComment)
                .leftJoin(subComment.member, member)
                .where(subComment.board.id.eq(boardId))
                .fetch();
    }
}
