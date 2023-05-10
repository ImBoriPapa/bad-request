package com.study.badrequest.dto.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class QuestionResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create {
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime askedAt;
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
        private Long id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime deletedAt;
    }
}
