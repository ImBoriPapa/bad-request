package com.study.badrequest.domain.board.service;


import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardCommendService {

    BoardResponse.Create create(User user, BoardRequest.Create form);

    BoardResponse.Update update(User user, Long boardId, BoardRequest.Update form);

    void delete(User user, Long boardId);


}
