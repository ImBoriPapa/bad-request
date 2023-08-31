package com.study.badrequest.question.query.dto;

import com.study.badrequest.question.command.domain.values.QuestionSortType;
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
    private QuestionSortType sort;
}
