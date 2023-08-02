package com.study.badrequest.question.command.domain;

import com.study.badrequest.common.status.ExposureStatus;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "question_metrics", indexes = {
        @Index(name = "VIEW_IDX", columnList = "count_of_view"),
        @Index(name = "RECOMMEND_IDX", columnList = "count_of_recommend"),
        @Index(name = "ANSWER_IDX", columnList = "count_of_answer"),
        @Index(name = "EXPOSURE_IDX", columnList = "exposure")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id")
public class QuestionMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_metrics_id")
    private Long id;
    @Column(name = "count_of_recommend")
    private Integer countOfRecommend;
    @Column(name = "count_of_view")
    private Integer countOfView;
    @Column(name = "count_of_answer")
    private Integer countOfAnswer;
    @Enumerated(EnumType.STRING)
    @Column(name = "exposure")
    private ExposureStatus exposure;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "questionMetrics")
    private Question question;

    protected QuestionMetrics(Integer countOfRecommend, Integer countOfView, Integer countOfAnswer, ExposureStatus exposure) {
        this.countOfRecommend = countOfRecommend;
        this.countOfView = countOfView;
        this.countOfAnswer = countOfAnswer;
        this.exposure = exposure;
    }

    public static QuestionMetrics createQuestionMetrics() {
        return new QuestionMetrics(0, 0, 0, ExposureStatus.PUBLIC);
    }

    public void addQuestion(Question question) {
        this.question = question;
    }

    public void incrementCountOfRecommendations() {
        ++this.countOfRecommend;
    }

    public void decrementCountOfRecommendations() {
        --this.countOfRecommend;
    }

    public void incrementCountOfView() {
        ++this.countOfView;
    }

    public void changeExposure(ExposureStatus exposureStatus) {
        this.exposure = exposureStatus;
    }

}
