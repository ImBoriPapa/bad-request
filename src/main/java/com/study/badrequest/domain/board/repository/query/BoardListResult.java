package com.study.badrequest.domain.board.repository.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardListResult extends RepresentationModel{

     private Long boardId;
     private Long memberId;
     private String profileImage;
     private String nickname;
     private String title;
     private Integer likeCount;
     private Category category;
     private Topic topic;
     private Integer commentCount;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
     private LocalDateTime createdAt;

}
