package com.study.badrequest.domain.comment.repository;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByBoard(Board board);
    Optional<Comment> findByBoard(Board findBoard);
    void deleteAllByBoard(Board board);
}
