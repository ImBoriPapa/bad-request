package com.study.badrequest.question.command.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateQuestionForm {
    private Long memberId;
    private String title;
    private String contents;
    private List<Long> tagIds;
    private List<Long> imageIds;
}
