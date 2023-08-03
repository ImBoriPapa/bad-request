package com.study.badrequest.question.query.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class QuestionCommentRequest {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Add{
        private String contents;
    }
}
