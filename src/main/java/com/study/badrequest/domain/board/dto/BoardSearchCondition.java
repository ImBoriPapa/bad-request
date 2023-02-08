package com.study.badrequest.domain.board.dto;

import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
