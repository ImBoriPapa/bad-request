package com.study.badrequest.domain.board.repository.query;


import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.Category;

import java.util.Optional;

public interface BoardQueryRepository {

    Optional<BoardDetailDto> findBoardDetail(Long boardId, Category category);

    BoardListDto findBoardList(BoardSearchCondition condition);
}
