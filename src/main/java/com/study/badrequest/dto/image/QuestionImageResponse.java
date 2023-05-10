package com.study.badrequest.dto.image;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class QuestionImageResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Temporary {
        private Long id;
        private String originalFileName;
        private String imageLocation;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime savedAt;
    }
}
