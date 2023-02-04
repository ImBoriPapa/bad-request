package com.study.badrequest.domain.board.repository.query;


import com.study.badrequest.domain.comment.entity.Comment;
import com.study.badrequest.domain.comment.entity.SubComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentDto {
    private Long commentId;
    private Long boardId;
    private Long memberId;
    private String nickname;
    private String text;
    private List<BoardSubCommentDto> subComment = new ArrayList<>();

    public BoardCommentDto(Comment comment) {
        this.commentId = comment.getId();
        this.boardId = comment.getBoard().getId();
        this.memberId = comment.getMember().getId();
        this.nickname = comment.getMember().getNickname();
        this.text = comment.getText();
        this.subComment = comment
                .getSubCommentList()
                .stream()
                .map(BoardSubCommentDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class BoardSubCommentDto {
        private Long id;
        private Long commentId;
        private Long boardId;
        private Long memberId;
        private String nickname;
        private String text;

        public BoardSubCommentDto(SubComment comment) {
            this.id = comment.getId();
            this.commentId = comment.getId();
            this.boardId = comment.getBoard().getId();
            this.memberId = comment.getMember().getId();
            this.nickname = comment.getMember().getNickname();
            this.text = comment.getText();

        }
    }

}
