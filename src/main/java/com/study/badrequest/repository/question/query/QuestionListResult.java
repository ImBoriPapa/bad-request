package com.study.badrequest.repository.question.query;

import com.study.badrequest.domain.question.QuestionSort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class QuestionListResult extends EntityModel {
    private Integer size;
    private Boolean hasNext;
    private QuestionSort sortBy;
    private Long lastOfIndex;
    private Integer lastOfView;
    private Integer lastOfRecommend;
    private List<QuestionDto> results = new ArrayList<>();

    public QuestionListResult(Integer size, Boolean hasNext, QuestionSort sortBy, Long lastOfIndex, Integer lastOfView, Integer lastOfRecommend, List<QuestionDto> results) {
        this.size = size;
        this.hasNext = hasNext;
        this.sortBy = sortBy;
        this.lastOfIndex = lastOfIndex;
        this.lastOfView = lastOfView;
        this.lastOfRecommend = lastOfRecommend;
        this.results = results;
    }
}
