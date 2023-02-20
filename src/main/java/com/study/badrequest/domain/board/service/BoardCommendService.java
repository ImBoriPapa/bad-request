package com.study.badrequest.domain.board.service;


import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardCommendService {

    BoardResponse.Create create(String username, BoardRequest.Create form, List<MultipartFile> images);

    BoardResponse.Update update(String username, Long boardId, BoardRequest.Update form, List<MultipartFile> images);

    void delete(Long boardId);

    void deleteAll(List<Long> boardId);

}
