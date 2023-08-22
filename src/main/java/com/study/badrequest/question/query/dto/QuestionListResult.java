package com.study.badrequest.question.query.dto;

import com.study.badrequest.question.command.domain.QuestionSortType;
import com.study.badrequest.question.query.dto.QuestionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionListResult {
    private Integer size;
    private Boolean hasNext;
    private QuestionSortType sortBy;
    private Long lastOfData;
    private List<QuestionDto> results = new ArrayList<>();

    public QuestionListResult(Integer size, Boolean hasNext, QuestionSortType sortBy, Long lastOfData, List<QuestionDto> results) {
        this.size = size;
        this.hasNext = hasNext;
        this.sortBy = sortBy;
        this.lastOfData = lastOfData;
        this.results = results;
    }
}
