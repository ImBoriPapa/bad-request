package com.study.badrequest.domain.comment.repository;

import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment,Long> {
    void deleteAllByBoard(Board board);

    List<SubComment> findAllByBoard(Board board);

    List<SubComment> findAllByComment(Comment comment);

    void deleteAllByComment(Comment comment);
}
