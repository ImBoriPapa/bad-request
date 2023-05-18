package com.study.badrequest.dto.questionComment;

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
