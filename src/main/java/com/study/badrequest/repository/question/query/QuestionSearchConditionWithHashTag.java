package com.study.badrequest.repository.question.query;

import com.study.badrequest.domain.question.QuestionSort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QuestionSearchConditionWithHashTag {

    private Long lastIndex;
    private Integer size;
    private QuestionSort sort;
    private Boolean isAnswered;
    private String tag;

}
