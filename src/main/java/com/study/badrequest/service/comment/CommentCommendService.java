package com.study.badrequest.service.comment;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.response.ApiResponseStatus;

import com.study.badrequest.domain.board.Board;
import com.study.badrequest.domain.comment.Comment;
import com.study.badrequest.domain.comment.SubComment;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.dto.comment.CommentRequest;
import com.study.badrequest.dto.comment.CommentResponse;
import com.study.badrequest.exception.custom_exception.BoardExceptionBasic;
import com.study.badrequest.exception.custom_exception.CommentExceptionBasic;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import com.study.badrequest.repository.board.BoardRepository;
import com.study.badrequest.repository.comment.CommentRepository;
import com.study.badrequest.repository.comment.SubCommentRepository;
import com.study.badrequest.repository.member.MemberRepository;

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
    public CommentResponse.Create addComment(Long boardId, String changeableId, CommentRequest.Create request) {

        Board board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new BoardExceptionBasic(ApiResponseStatus.NOT_FOUND_BOARD));



        Member member = memberRepository
                .findMemberByChangeableIdAndCreateDateTimeIndex(changeableId, Member.getCreatedAtInChangeableId(changeableId))
                .orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));

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
     * 댓글 수정
     */
    @CustomLogTracer
    public CommentResponse.Modify modifyComment(Long commentId, String text) {

        commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentExceptionBasic(ApiResponseStatus.NOT_FOUND_COMMENT))
                .modify(text);

        // TODO: 2023/02/06 쿼리 최적화
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentExceptionBasic(ApiResponseStatus.NOT_FOUND_COMMENT));

        return new CommentResponse.Modify(findComment.getId(), findComment.getUpdatedAt());
    }

    /**
     * 댓글 삭제시 Sub 댓글 삭제
     * -> 게시판의 댓글 감소
     */
    @CustomLogTracer
    public CommentResponse.Delete deleteComment(Long commentId) {
        //댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentExceptionBasic(ApiResponseStatus.NOT_FOUND_COMMENT));

        comment.getBoard().decreaseCommentCount();

        subCommentRepository.findAllByComment(comment)
                .forEach(s -> s.getComment().getBoard().decreaseCommentCount());

        subCommentRepository.deleteAllByComment(comment);

        commentRepository.delete(comment);

        return new CommentResponse.Delete(true, LocalDateTime.now());
    }

    /**
     * 게시판 삭제시 게시판의 댓글 모두 삭제
     */
    @CustomLogTracer
    public void deleteAllCommentsAndSubCommentsByBoardId(Long boardId) {
        Board board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new BoardExceptionBasic(ApiResponseStatus.NOT_FOUND_BOARD));

        subCommentRepository.deleteAllByBoard(board);

        commentRepository.deleteAllByBoard(board);

    }

    /**
     * 대댓글
     */
    @CustomLogTracer
    public CommentResponse.CreateSub addSubComment(Long commentId, String changeableId, CommentRequest.Create request) {

        Member member = memberRepository
                .findMemberByChangeableIdAndCreateDateTimeIndex(changeableId, Member.getCreatedAtInChangeableId(changeableId))
                .orElseThrow(() -> new MemberExceptionBasic(ApiResponseStatus.NOTFOUND_MEMBER));

        Comment findComment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new BoardExceptionBasic(ApiResponseStatus.NOT_FOUND_BOARD));

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
                .orElseThrow(() -> new CommentExceptionBasic(ApiResponseStatus.NOT_FOUND_COMMENT))
                .modify(text);

        SubComment subComment = subCommentRepository.findById(subCommentId).orElseThrow(() -> new CommentExceptionBasic(ApiResponseStatus.NOT_FOUND_SUB_COMMENT));

        return new CommentResponse.ModifySub(subComment.getId(), subComment.getUpdatedAt());
    }

    @CustomLogTracer
    public CommentResponse.DeleteSub deleteSubComment(Long subCommentId) {
        SubComment subComment = subCommentRepository.findById(subCommentId)
                .orElseThrow(() -> new CommentExceptionBasic(ApiResponseStatus.NOT_FOUND_SUB_COMMENT));

        subComment.getBoard().decreaseCommentCount();
        subComment.getComment().decreaseSubCount();

        subCommentRepository.delete(subComment);

        return new CommentResponse.DeleteSub(true, LocalDateTime.now());
    }
}
