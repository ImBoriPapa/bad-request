package com.study.badrequest.domain.comment.repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.comment.entity.SubComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentDto extends RepresentationModel {

    private Long subCommentId;
    private Long commentId;
    private Long boardId;
    private Long memberId;

    private String profileImage;
    private String nickname;
    private String text;

    private Integer likeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
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
