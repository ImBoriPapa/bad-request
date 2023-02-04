package com.study.badrequest.domain.board.repository.query;

import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

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
     private String createdAt;

}
