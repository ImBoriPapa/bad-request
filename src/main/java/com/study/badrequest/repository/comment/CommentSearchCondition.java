package com.study.badrequest.repository.comment;

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
