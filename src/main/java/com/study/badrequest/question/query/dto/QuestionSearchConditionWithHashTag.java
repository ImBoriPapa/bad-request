package com.study.badrequest.question.query.dto;

import com.study.badrequest.question.command.domain.QuestionSortType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QuestionSearchConditionWithHashTag {

    private Long lastIndex;
    private Integer size;
    private QuestionSortType sort;
    private Boolean isAnswered;
    private String tag;

}
