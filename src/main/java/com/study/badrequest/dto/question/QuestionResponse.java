package com.study.badrequest.dto.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class QuestionResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Create{
        private Long id;
        private LocalDateTime askedAt;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Modify{
        private Long id;
        private LocalDateTime modifiedAt;
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Delete {
        private Long id;
        private LocalDateTime deletedAt;
    }
}
