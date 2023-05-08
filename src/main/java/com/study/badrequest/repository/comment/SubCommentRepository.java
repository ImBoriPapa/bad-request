package com.study.badrequest.repository.comment;


import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.comment.Comment;
import com.study.badrequest.domain.comment.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCommentRepository extends JpaRepository<SubComment,Long> {
    void deleteAllByBoard(Board board);

    List<SubComment> findAllByBoard(Board board);

    List<SubComment> findAllByComment(Comment comment);

    void deleteAllByComment(Comment comment);
}
