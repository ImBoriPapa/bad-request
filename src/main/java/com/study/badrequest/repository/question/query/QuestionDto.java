package com.study.badrequest.repository.question.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class QuestionDto {
    private Long id;
    private String title;
    private String preview;
    private Boolean isAnswered;
    private QuestionMetrics metrics;
    private Questioner questioner;
    private List<HashTagDto> hashTag = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime askedAt;
    public void addHashTag(List<HashTagDto> hashTags) {
        this.hashTag = hashTags;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class QuestionMetrics {
        private Integer countOfRecommend;
        private Integer countOfView;

    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Questioner{
        private Long id;
        private String nickname;
        private String profileImage;

        private Integer activityScore;

    }

}
