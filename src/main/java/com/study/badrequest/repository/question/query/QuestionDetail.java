package com.study.badrequest.repository.question.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.badrequest.domain.recommendation.RecommendationKind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class QuestionDetail {
    private Long id;
    private String title;
    private String contents;
    private Boolean isQuestioner;
    private QuestionDetailMetrics metrics;
    private QuestionDetailQuestioner questioner;
    private List<TagDto> tag = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime askedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;

    public void addTag(List<TagDto> tags) {
        this.tag = tags;
    }

    public void isQuestionerToTrue() {
        this.isQuestioner = true;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class QuestionDetailMetrics {
        private Integer countOfRecommend;
        private Integer countOfView;
        private Integer countOfAnswer;
        private Boolean hasRecommendation;
        private RecommendationKind kind;
        public void setHasRecommendationAndKind(boolean hasRecommendation,RecommendationKind kind) {
            this.hasRecommendation = hasRecommendation;
            this.kind = kind;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class QuestionDetailQuestioner {
        private Long id;
        private String nickname;
        private String profileImage;
        private Integer activityScore;

    }


}
