package com.study.badrequest.domain.comment.repository.dto;

import com.study.badrequest.domain.comment.entity.SubComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentDto {

    private Long subCommentId;
    private Long commentId;
    private Long boardId;
    private Long memberId;

    private String profileImage;
    private String nickname;
    private String text;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public SubCommentDto(SubComment subComment) {
        this.subCommentId = subComment.getId();
        this.commentId = subComment.getComment().getId();
        this.boardId = subComment.getBoard().getId();
        this.memberId = subComment.getMember().getId();
        this.nickname = subComment.getMember().getNickname();
        this.text = subComment.getText();
    }
}
