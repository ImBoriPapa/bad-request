package com.study.badrequest.question.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class QuestionDto extends EntityModel {
    private Long id;
    private String title;
    private String preview;
    private Metrics metrics;
    private Questioner questioner;
    private List<TagDto> tags = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime askedAt;
    public void addTags(List<TagDto> tags) {
        this.tags = tags;
    }
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Metrics {
        private Integer countOfRecommend;
        private Integer countOfView;
        private Integer countOfAnswer;

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
