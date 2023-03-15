package com.study.badrequest.domain.board.repository;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.entity.BoardImageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage,Long> {
    List<BoardImage> findByBoard(Board board);

    List<BoardImage> findByStatus(BoardImageStatus temporary);
}
