package com.study.badrequest.answer.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.recommandation.command.domain.RecommendationKind;
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
    private Boolean isAnswerer;
    private Answerer answerer;
    private Metrics metrics;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime answeredAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answerer {
        private Long id;
        private String nickname;
        private String profileImage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metrics {
        private Integer numberOfRecommendation;
        private Integer numberOfComment;
        private Boolean hasRecommendation;
        private RecommendationKind kind;

        public void setHasRecommendation(RecommendationKind kind) {
            this.hasRecommendation = true;
            this.kind = kind;
        }

    }
}