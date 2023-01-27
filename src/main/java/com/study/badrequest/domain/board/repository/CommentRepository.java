package com.study.badrequest.domain.board.repository;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByBoard(Board board);
}
