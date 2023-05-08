package com.study.badrequest.repository.board.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.board.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDetailDto {
    private Long id;
    private Long memberId;
    private String profileImage;
    private String nickname;
    private String title;
    private String contents;
    private Integer likeCount;
    private Category category;
    private Topic topic;
    private Integer commentCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

}
