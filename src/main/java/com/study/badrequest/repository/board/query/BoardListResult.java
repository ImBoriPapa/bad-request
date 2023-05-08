package com.study.badrequest.repository.board.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.board.BoardTag;
import com.study.badrequest.domain.board.Category;
import com.study.badrequest.domain.board.HashTag;
import com.study.badrequest.domain.board.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListResult extends RepresentationModel {
    // TODO: 2023/04/27 본인 확인, 변수명 변경
    private Long id;
    private Long memberId;
    private String profileImage;
    private String nickname;
    private String title;
    private Integer likeCount;
    private Category category;
    private Topic topic;
    private Integer commentCount;
    private List<TagDto> hashTags = new ArrayList<>();
    private boolean isMyBoard;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    public void setIsMyBoard(Boolean isMyBoard) {
        this.isMyBoard = isMyBoard;
    }
    public void setHashTags(List<TagDto> hashTags) {
        this.hashTags = hashTags;
    }
}
