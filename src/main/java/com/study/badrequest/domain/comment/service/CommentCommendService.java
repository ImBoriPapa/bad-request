package com.study.badrequest.domain.comment.service;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.entity.SubComment;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.repository.SubCommentRepository;
import com.study.badrequest.commons.exception.custom_exception.BoardException;
import com.study.badrequest.commons.exception.custom_exception.CommentException;
import com.study.badrequest.commons.exception.custom_exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentCommendService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final SubCommentRepository subCommentRepository;
    private final BoardRepository boardRepository;

    @CustomLogTracer
    public CommentResponse.Create addComment(Long boardId, String username, CommentRequest.Create request) {

        Board board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));

        Member member = memberRepository
                .findMemberByUsername(username)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));

        Comment comment = Comment
                .createComment()
                .text(request.getText())
                .member(member)
                .board(board)
                .build();

        board.increaseCommentCount();

        Comment saveComment = commentRepository.save(comment);

        return new CommentResponse.Create(saveComment.getId(), saveComment.getCreatedAt());
    }

    /*
     * ?????? ??????
     */
    @CustomLogTracer
    public CommentResponse.Modify modifyComment(Long commentId, String text) {

        commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CustomStatus.NOT_FOUND_COMMENT))
                .modify(text);

        // TODO: 2023/02/06 ?????? ?????????
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentException(CustomStatus.NOT_FOUND_COMMENT));

        return new CommentResponse.Modify(findComment.getId(), findComment.getUpdatedAt());
    }

    /**
     * ?????? ????????? Sub ?????? ??????
     * -> ???????????? ?????? ??????
     */
    @CustomLogTracer
    public CommentResponse.Delete deleteComment(Long commentId) {
        //?????? ??????
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CustomStatus.NOT_FOUND_COMMENT));

        comment.getBoard().decreaseCommentCount();

        subCommentRepository.findAllByComment(comment)
                .forEach(s -> s.getComment().getBoard().decreaseCommentCount());

        subCommentRepository.deleteAllByComment(comment);

        commentRepository.delete(comment);

        return new CommentResponse.Delete(true, LocalDateTime.now());
    }

    /**
     * ????????? ????????? ???????????? ?????? ?????? ??????
     */
    @CustomLogTracer
    public void deleteAllCommentsAndSubCommentsByBoardId(Long boardId) {
        Board board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));

        subCommentRepository.deleteAllByBoard(board);

        commentRepository.deleteAllByBoard(board);

    }

    /**
     * ?????????
     */
    @CustomLogTracer
    public CommentResponse.CreateSub addSubComment(Long commentId, String username, CommentRequest.Create request) {

        Member member = memberRepository.findMemberByUsername(username)
                .orElseThrow(() -> new MemberException(CustomStatus.NOTFOUND_MEMBER));

        Comment findComment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new BoardException(CustomStatus.NOT_FOUND_BOARD));

        SubComment subComment = SubComment
                .CreateSubComment()
                .comment(findComment)
                .member(member)
                .board(findComment.getBoard())
                .text(request.getText())
                .build();

        SubComment save = subCommentRepository.save(subComment);

        save.getBoard().increaseCommentCount();
        save.getComment().increaseSubCount();

        return new CommentResponse.CreateSub(save.getId(), save.getCreatedAt());
    }

    @CustomLogTracer
    public CommentResponse.ModifySub modifySubComment(Long subCommentId, String text) {
        subCommentRepository.findById(subCommentId)
                .orElseThrow(() -> new CommentException(CustomStatus.NOT_FOUND_COMMENT))
                .modify(text);

        SubComment subComment = subCommentRepository.findById(subCommentId).orElseThrow(() -> new CommentException(CustomStatus.NOT_FOUND_SUB_COMMENT));

        return new CommentResponse.ModifySub(subComment.getId(), subComment.getUpdatedAt());
    }

    @CustomLogTracer
    public CommentResponse.DeleteSub deleteSubComment(Long subCommentId) {
        SubComment subComment = subCommentRepository.findById(subCommentId)
                .orElseThrow(() -> new CommentException(CustomStatus.NOT_FOUND_SUB_COMMENT));

        subComment.getBoard().decreaseCommentCount();
        subComment.getComment().decreaseSubCount();

        subCommentRepository.delete(subComment);

        return new CommentResponse.DeleteSub(true, LocalDateTime.now());
    }
}
