package com.study.badrequest.repository.comment.query;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class CommentDto extends RepresentationModel{
    private Long commentId;
    private Long boardId;
    private Long memberId;
    private String profileImage;
    private String nickname;
    private String text;
    private Integer likeCount;
    private Integer subCommentCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

}
