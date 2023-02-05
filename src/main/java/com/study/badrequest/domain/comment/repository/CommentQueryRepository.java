package com.study.badrequest.domain.comment.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.comment.repository.dto.CommentDto;
import com.study.badrequest.domain.comment.repository.dto.SubCommentDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.badrequest.domain.Member.domain.entity.QMember.member;
import static com.study.badrequest.domain.comment.entity.QComment.comment;
import static com.study.badrequest.domain.comment.entity.QSubComment.*;


@Repository
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * Member Entity , SubComment Entity fetch 조인 후 한방 쿼리로 조회
     * 장점: 한번 쿼리
     * 단점 1:  1:N 관계로 Projection 불가
     * 단점 2:  Comment,SubComment 일대다 ,다대일 양방향 관계
     */
    public List<CommentDto> findCommentByBoardJustOneQuery(Long boardId) {
        log.info("=================[getComments QUERY START]=================");
        List<CommentDto> collect = jpaQueryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.member)
                .fetchJoin()
                .leftJoin(comment.subCommentList)
                .fetchJoin()
                .where(comment.board.id.eq(boardId))
                .distinct()
                .fetch()
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
        log.info("=================[getComments QUERY FINISH]=================");

        return collect;
    }

    /**
     * Comment Entity Member Entity 조인, SubComment Entity Member Entity 조인 후 각각 조회 후 반환
     * 장점1 : Projections 으로 필요한 필드만 조회가능
     * 장점2 : Comment,SubComment 다대일 단방향 설계 가능
     * 단점: 쿼리가 두번 나감
     */
    public List<CommentDto> findCommentByBoardUseTwoQuery(Long boardId) {
        log.info("=================[getComments QUERY START]=================");
        List<CommentDto> commentDtoList = jpaQueryFactory
                .select(Projections.fields(CommentDto.class,
                        comment.id.as("commentId"),
                        comment.board.id.as("boardId"),
                        comment.member.id.as("memberId"),
                        comment.member.profileImage.fullPath.as("profileImage"),
                        comment.member.nickname.as("nickname"),
                        comment.text.as("text"),
                        comment.createdAt.as("createdAt"),
                        comment.updatedAt.as("updatedAt")
                ))
                .from(comment)
                .leftJoin(comment.member, member)
                .where(comment.board.id.eq(boardId))
                .fetch();

        List<SubCommentDto> subCommentDtoList = jpaQueryFactory
                .select(Projections.fields(SubCommentDto.class,
                        subComment.id.as("subCommentId"),
                        subComment.comment.id.as("commentId"),
                        subComment.board.id.as("boardId"),
                        subComment.member.id.as("memberId"),
                        subComment.member.profileImage.fullPath.as("profileImage"),
                        subComment.member.nickname.as("nickname"),
                        subComment.text.as("text"),
                        subComment.createdAt.as("createdAt"),
                        subComment.updatedAt.as("updatedAt")
                ))
                .from(subComment)
                .leftJoin(subComment.member, member)
                .where(subComment.board.id.eq(boardId))
                .fetch();

        commentDtoList
                .forEach(list -> addSubToComment(list, subCommentDtoList));
        log.info("=================[getComments QUERY FINISH]=================");

        return commentDtoList;
    }

    private void addSubToComment(CommentDto commentDto, List<SubCommentDto> subCommentDtoList) {
        if (commentDto.getCommentId() != null)
            for (SubCommentDto subCommentDto : subCommentDtoList) {
                if (subCommentDto.getCommentId() == commentDto.getCommentId()) {
                    commentDto.addSub(subCommentDto);
                }
            }
    }
}
