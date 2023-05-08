package com.study.badrequest.repository.board;


import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.board.BoardImage;
import com.study.badrequest.domain.board.BoardImageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage,Long> {
    List<BoardImage> findAllByBoardAndStatus(Board board, BoardImageStatus status);

    List<BoardImage> findByStatus(BoardImageStatus temporary);

    List<BoardImage> findAllByIdInAndStatus(List<Long> imageIds, BoardImageStatus temporary);
}
