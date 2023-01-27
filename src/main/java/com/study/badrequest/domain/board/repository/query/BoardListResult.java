package com.study.badrequest.domain.board.repository.query;

import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardListResult {

     private Long boardId;
     private String nickname;
     private String title;
     private Integer likeCount;
     private Category category;
     private Topic topic;
     private String createdAt;

}
