package com.study.badrequest.question.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateQuestionWriterForm {
    private Long memberId;
    private String nickname;
    private String profileImage;
    private Integer activeScore;
}
