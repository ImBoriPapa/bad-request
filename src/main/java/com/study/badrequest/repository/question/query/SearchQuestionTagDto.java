package com.study.badrequest.repository.question.query;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SearchQuestionTagDto {
    private Long id;
    private Long questionId;
    private String tagName;
}
