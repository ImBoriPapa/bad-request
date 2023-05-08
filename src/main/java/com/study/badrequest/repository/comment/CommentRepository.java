package com.study.badrequest.repository.comment;


import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByBoard(Board board);
    Optional<Comment> findByBoard(Board findBoard);
    void deleteAllByBoard(Board board);
}
