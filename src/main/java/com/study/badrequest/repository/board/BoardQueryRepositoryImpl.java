package com.study.badrequest.repository.board;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.board.*;
import com.study.badrequest.dto.board.BoardSearchCondition;
import com.study.badrequest.repository.board.query.BoardDetailDto;
import com.study.badrequest.repository.board.query.BoardListDto;
import com.study.badrequest.repository.board.query.BoardListResult;
import com.study.badrequest.repository.board.query.TagDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import static com.study.badrequest.domain.board.QBoard.board;


import static com.study.badrequest.domain.board.QBoardTag.*;
import static com.study.badrequest.domain.board.QHashTag.*;
import static com.study.badrequest.domain.member.QMember.*;
import static com.study.badrequest.domain.member.QMemberProfile.*;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * BOARD_CATEGORY_IDX 인덱스를 사용하여 검색
     */
    @Override
    public Optional<BoardDetailDto> findBoardDetailByIdAndCategory(Long boardId, Category category) {
        return jpaQueryFactory
                .select(
                        Projections.fields(BoardDetailDto.class,
                                board.id.as("id"),
                                member.id.as("memberId"),
                                member.memberProfile.profileImage.imageLocation.as("profileImage"),
                                member.memberProfile.nickname.as("nickname"),
                                board.title.as("title"),
                                board.contents.as("contents"),
                                board.likeCount.as("likeCount"),
                                board.category.as("category"),
                                board.topic.as("topic"),
                                board.commentCount.as("commentCount"),
                                board.createdAt.as("createdAt"),
                                board.updatedAt.as("updatedAt"))
                )
                .from(board)
                .join(board.member, member)
                .join(member.memberProfile, memberProfile)
                .where(board.id.eq(boardId), eqCategory(category))
                .fetch()
                .stream()
                .findFirst();
    }

    @Override
    public BoardListDto findBoardList(BoardSearchCondition condition,Long loginMemberId) {
        int limitSize = setSize(condition.getSize());
        long lastIndex = setLastIndex(condition.getLastIndex());
        int resultSize;
        boolean hasNext;

        log.info("[findBoardList QUERY START]");
        List<BoardListResult> results = jpaQueryFactory
                .select(Projections.fields(BoardListResult.class,
                        board.id.as("id"),
                        member.id.as("memberId"),
                        memberProfile.profileImage.imageLocation.as("profileImage"),
                        memberProfile.nickname.as("nickname"),
                        board.title.as("title"),
                        board.likeCount.as("likeCount"),
                        board.category.as("category"),
                        board.topic.as("topic"),
                        board.commentCount.as("commentCount"),
                        board.createdAt.as("createdAt")
                ))
                .from(board)
                .join(board.member,member)
                .join(member.memberProfile,memberProfile)
                .where(cursor(lastIndex),
                        eqCategory(condition.getCategory()),
                        containsTitle(condition.getTitle()),
                        eqTopic(condition.getTopic()),
                        eqNickname(condition.getNickname()),
                        eqMember(condition.getMemberId())
                )
                .orderBy(board.id.desc())
                //요청 데이터 size 보다 +1 조회
                .limit(limitSize + 1)
                .fetch();

        log.info("[findBoardList QUERY FINISH]");
        //현재 로그인된 회원이 작성한 게시글 표시
        if(!results.isEmpty() && loginMemberId != null){
            results.stream()
                    .filter(r->r.getMemberId().equals(loginMemberId))
                    .collect(Collectors.toList())
                    .forEach(r->r.setIsMyBoard(true));

        }

        List<Long> boardIds = results.stream()
                .map(BoardListResult::getId)
                .collect(Collectors.toList());

        List<BoardTag> boardTagList = jpaQueryFactory
                .select(boardTag)
                .from(boardTag)
                .join(boardTag.hashTag, hashTag)
                .where(boardTag.board.id.in(boardIds))
                .fetch();

        Map<Long, List<BoardTag>> listMap = boardTagList.stream()
                .collect(Collectors.groupingBy(boardTag -> boardTag.getBoard().getId()));

        results.forEach(boardListResult->
                    boardListResult.setHashTags(
                            listMap.get(boardListResult.getId()).stream()
                            .map(boardTag-> new TagDto(
                                    boardTag.getId(),
                                    boardTag.getHashTag().getHashTagName()
                            )).collect(Collectors.toList()))
                );

        resultSize = results.size();
        // resultSize > limitSize 다음 데이터 존재
        // resultSize > limitSize 다음 페이터 없음
        hasNext = resultSize > limitSize;

        // 다음 데이터가 있을 경우 1개 조회한 결과에서 마지막 데이터  삭제
        if (hasNext) {
            results.remove(limitSize);
            --resultSize;
        }

        //검색 결과에서 제일 작은 인덱스
        // TODO: 2023/02/02 검색결과 정렬 바꿀때는?
        OptionalLong resultLastIndex = results
                .stream()
                .mapToLong(BoardListResult::getId)
                .min();

        return new BoardListDto(resultSize, hasNext, resultLastIndex.orElse(0L), results);
    }

    private Integer setSize(Integer size) {
        return size == null ? 10 : size;
    }

    private Long setLastIndex(Long lastIndex) {
        return lastIndex == null ? 0L : lastIndex;
    }

    private BooleanExpression cursor(Long boardId) {
        return boardId <= 0 ? null : QBoard.board.id.lt(boardId);
    }

    private BooleanExpression eqCategory(Category category) {
        return category != null ? board.category.eq(category) : null;
    }

    private BooleanExpression containsTitle(String keyword) {
        return keyword != null ? board.title.contains(keyword) : null;
    }

    private BooleanExpression eqTopic(Topic topic) {
        return topic != null ? board.topic.eq(topic) : null;
    }

    private BooleanExpression eqNickname(String nickname) {
        return nickname != null ? board.member.memberProfile.nickname.eq(nickname) : null;
    }

    private BooleanExpression eqMember(Long memberId) {
        return memberId != null ? board.member.isNotNull().and(board.member.id.eq(memberId)) : null;
    }
}
