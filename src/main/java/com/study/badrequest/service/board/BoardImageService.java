package com.study.badrequest.service.board;


import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.board.BoardImageStatus;
import com.study.badrequest.dto.board.BoardImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardImageService {

    BoardImageResponse.Create save(MultipartFile image);

    void update(List<Long> imageIds, Board board);

    void changeStatus(List<Long> ids, Board board, BoardImageStatus status);

    void deleteByBoard(Board board);

    void clearTemporaryImage();

}
