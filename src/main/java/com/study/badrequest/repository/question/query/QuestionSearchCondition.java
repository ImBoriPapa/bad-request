package com.study.badrequest.repository.question.query;

import com.study.badrequest.domain.question.QuestionSort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionSearchCondition {
    private Long lastOfData;
    private Integer size;
    private QuestionSort sort;
}
