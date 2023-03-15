package com.study.badrequest.domain.board.service;

import com.study.badrequest.domain.board.dto.BoardImageResponse;
import com.study.badrequest.domain.board.entity.Board;

import com.study.badrequest.domain.board.entity.BoardImageStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardImageService {

    BoardImageResponse.Create save(MultipartFile image);

    void update(List<Long> imageIds, Board board);

    void changeStatus(List<Long> ids, Board board, BoardImageStatus status);

    void deleteByBoard(Board board);

    void clearTemporaryImage();

}
