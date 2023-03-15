package com.study.badrequest.domain.board.repository;


import com.study.badrequest.domain.board.dto.BoardSearchCondition;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.repository.query.BoardDetailDto;
import com.study.badrequest.domain.board.repository.query.BoardListDto;

import java.util.Optional;

public interface BoardQueryRepository {

    Optional<BoardDetailDto> findBoardDetailByIdAndCategory(Long boardId , Category category);

    BoardListDto findBoardList(BoardSearchCondition condition);
}
