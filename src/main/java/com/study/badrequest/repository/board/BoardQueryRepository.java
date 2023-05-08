package com.study.badrequest.repository.board;


import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.dto.board.BoardSearchCondition;
import com.study.badrequest.repository.board.query.BoardDetailDto;
import com.study.badrequest.repository.board.query.BoardListDto;

import java.util.Optional;

public interface BoardQueryRepository {

    Optional<BoardDetailDto> findBoardDetailByIdAndCategory(Long boardId , Category category);

    BoardListDto findBoardList(BoardSearchCondition condition,Long loginMemberId);

}
