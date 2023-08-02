package com.study.badrequest.answer.query.dto;

import com.study.badrequest.answer.query.dto.AnswerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AnswerResult {
    private Integer size;
    private Long lastOfData;
    private Boolean hasNext;
    private List<AnswerDto> answerDtoList = new ArrayList<>();

}
