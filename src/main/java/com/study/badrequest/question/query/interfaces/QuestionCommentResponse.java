package com.study.badrequest.question.query.interfaces;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class QuestionCommentResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Add {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime addedAT;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Delete{
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime deletedAt;
    }
}
