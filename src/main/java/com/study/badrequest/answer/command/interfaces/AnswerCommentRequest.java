package com.study.badrequest.answer.command.interfaces;

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
