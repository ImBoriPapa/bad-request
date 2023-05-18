package com.study.badrequest.repository.answer.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private Long id;
    private String contents;
    private Integer numberOfRecommendation;
    private AnswererDto answerer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime answeredAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswererDto {
        private Long id;
        private String nickname;
        private String profileImage;
    }
}