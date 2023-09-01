package com.study.badrequest.question.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


public record CreateQuestionRequest(
        Long memberId,
        String title,
        String contents,
        List<Long> tagIds,
        List<Long> imageIds) {
}
