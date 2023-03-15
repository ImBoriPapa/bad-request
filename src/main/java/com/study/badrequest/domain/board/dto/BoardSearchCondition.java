package com.study.badrequest.domain.board.dto;

import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.*;

@Getter
@Setter  //파라미터 바인딩을 위해 필요
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchCondition {
    private Integer size;
    private Long lastIndex;
    private String title;
    private Category category;
    private Topic topic;
    private String nickname;
    private Long memberId;
}
