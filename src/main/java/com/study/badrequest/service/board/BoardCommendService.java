package com.study.badrequest.service.board;


import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.dto.board.BoardRequest;
import com.study.badrequest.dto.board.BoardResponse;
import org.springframework.security.core.userdetails.User;

public interface BoardCommendService {

    BoardResponse.Create create(Long memberId, Authority authority, BoardRequest.Create form);

    BoardResponse.Update update(User user, Long boardId, BoardRequest.Update form);

    void delete(User user, Long boardId);


}
