package com.study.badrequest.dto.answerComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class AnswerCommentRequest {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Add{
        private String contents;
    }
}
