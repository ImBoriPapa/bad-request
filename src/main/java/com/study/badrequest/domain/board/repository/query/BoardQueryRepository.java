package com.study.badrequest.domain.board.repository.query;



import com.study.badrequest.domain.board.dto.BoardSearchCondition;

import java.util.Optional;

public interface BoardQueryRepository {

    Optional<BoardDetailDto> findBoardDetail(Long boardId);
     BoardListDto findBoardList(BoardSearchCondition condition);
}
