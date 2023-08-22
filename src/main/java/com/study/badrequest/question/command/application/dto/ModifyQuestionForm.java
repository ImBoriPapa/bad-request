package com.study.badrequest.question.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ModifyQuestionForm {
    private Long questionId;
    private Long requesterId;
    private String title;
    private String contents;
    private List<Long> imageIds;
}
