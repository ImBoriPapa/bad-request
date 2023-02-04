package com.study.badrequest.domain.comment.service;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentCommendService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @CustomLogger
    public Comment add(Long boardId, String text) {

        Board board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        Comment comment = Comment.builder()
                .text(text)
                .member(board.getMember())
                .board(board)
                .build();

        board.increaseCommentCount();

        return commentRepository.save(comment);
    }

    @CustomLogger
    public void modify(Long commentId, String text) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(""));
        comment.modify(text);
    }

    @CustomLogger
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException(""));

        comment.getBoard().decreaseCommentCount();

        commentRepository.delete(comment);

    }
}
