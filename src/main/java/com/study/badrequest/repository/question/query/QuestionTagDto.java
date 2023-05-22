package com.study.badrequest.repository.question.query;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuestionTagDto {
    private Long id;
    private Long questionId;
    private Long hashTagId;
    private String hashTagName;
}
