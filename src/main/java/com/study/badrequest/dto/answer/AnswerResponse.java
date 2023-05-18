package com.study.badrequest.dto.answer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class AnswerResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Register {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime answeredAt;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Modify {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime modifiedAt;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Delete {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime deletedAt;
    }
}
