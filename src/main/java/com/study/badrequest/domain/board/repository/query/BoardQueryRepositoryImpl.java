package com.study.badrequest.domain.board.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;


import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.*;

import com.study.badrequest.domain.comment.entity.QComment;
import com.study.badrequest.domain.comment.entity.QSubComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;


import static com.study.badrequest.domain.board.entity.QBoard.board;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BoardQueryRepositoryImpl implements BoardQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * BOARD_CATEGORY_IDX 인덱스를 사용하여 검색
     * Category 검색조건에 카테고리가 없으면 게시판 아이디로만 검색 인덱스 사용 x
     */
    @Override
    public Optional<BoardDetailDto> findBoardDetail(Long boardId, Category category) {
        log.info("[fetchOneBoard QUERY START]");
        Board findBoard = jpaQueryFactory
                .select(board)
                .from(board)
                .join(board.member)
                .fetchJoin()
                .leftJoin(board.boardImages)
                .fetchJoin()
                .where(board.id.eq(boardId), eqCategory(category))
                .distinct()
                .fetchOne();
        log.info("[findBoardDetail QUERY FINISH]");

        return findBoard == null ? Optional.empty() : Optional
                .ofNullable(BoardDetailDto.builder()
                        .board(findBoard)
                        .boardImages(findBoard.getBoardImages())
                        .build());
    }


    @Override
    public BoardListDto findBoardList(BoardSearchCondition condition) {
        int limitSize = setSize(condition.getSize());
        long lastIndex = setLastIndex(condition.getLastIndex());
        int resultSize = 0;

        log.info("[findBoardList QUERY START]");
        List<BoardListResult> results = jpaQueryFactory
                .select(Projections.fields(BoardListResult.class,
                        board.id.as("boardId"),
                        board.member.id.as("memberId"),
                        board.member.profileImage.fullPath.as("profileImage"),
                        board.member.nickname.as("nickname"),
                        board.title.as("title"),
                        board.likeCount.as("likeCount"),
                        board.category.as("category"),
                        board.topic.as("topic"),
                        board.commentCount.as("commentCount"),
                        board.createdAt.as("createdAt")
                ))
                .from(board)
                .where(cursor(lastIndex),
                        eqCategory(condition.getCategory()),
                        title(condition.getTitle()),
                        eqTopic(condition.getTopic()),
                        eqNickname(condition.getNickname()),
                        eqMember(condition.getMemberId())
                )
                .orderBy(board.id.desc())
                //요청 데이터 size 보다 +1 조회
                .limit(limitSize + 1)
                .fetch();
        log.info("[findBoardList QUERY FINISH]");

        resultSize = results.size();
        // resultSize > limitSize 다음 데이터 존재
        // resultSize > limitSize 다음 페이터 없음
        boolean hasNext = resultSize > limitSize;

        // 다음 데이터가 있을 경우 1개 조회한 결과에서 마지막 데이터  삭제
        if (hasNext) {
            results.remove(limitSize);
            --resultSize;
        }

        //검색 결과에서 제일 작은 인덱스
        // TODO: 2023/02/02 검색결과 정렬 바꿀때는?
        OptionalLong resultLastIndex = results
                .stream()
                .mapToLong(BoardListResult::getBoardId)
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

    private BooleanExpression title(String keyword) {
        return keyword != null ? board.title.contains(keyword) : null;
    }

    private BooleanExpression eqTopic(Topic topic) {
        return topic != null ? board.topic.eq(topic) : null;
    }

    private BooleanExpression eqNickname(String nickname) {
        return nickname != null ? board.member.nickname.eq(nickname) : null;
    }

    private BooleanExpression eqMember(Long memberId) {
        return memberId != null ? board.member.id.eq(memberId) : null;
    }
}
