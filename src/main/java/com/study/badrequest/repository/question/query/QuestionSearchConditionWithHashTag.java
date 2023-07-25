package com.study.badrequest.repository.question.query;

import com.study.badrequest.domain.question.QuestionSortType;
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
