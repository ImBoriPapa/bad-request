package com.study.badrequest.domain.board.repository;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {

    Optional<Board> findByTitle(String title);
}
