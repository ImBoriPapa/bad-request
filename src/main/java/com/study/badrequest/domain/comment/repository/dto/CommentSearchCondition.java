package com.study.badrequest.domain.comment.repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentSearchCondition {
    private Integer size;
    private Long lastIndex;
}
