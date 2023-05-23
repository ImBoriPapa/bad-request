package com.study.badrequest.repository.question.query;

import com.study.badrequest.domain.question.QuestionSort;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionListResult {
    private Integer size;
    private Boolean hasNext;
    private QuestionSort sortBy;
    private Long lastOfData;
    private List<QuestionDto> results = new ArrayList<>();

    public QuestionListResult(Integer size, Boolean hasNext, QuestionSort sortBy, Long lastOfData, List<QuestionDto> results) {
        this.size = size;
        this.hasNext = hasNext;
        this.sortBy = sortBy;
        this.lastOfData = lastOfData;
        this.results = results;
    }
}
