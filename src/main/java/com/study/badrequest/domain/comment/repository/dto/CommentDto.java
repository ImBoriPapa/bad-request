package com.study.badrequest.domain.comment.repository.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.comment.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
