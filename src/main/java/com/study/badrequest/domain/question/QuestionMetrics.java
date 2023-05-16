package com.study.badrequest.domain.question;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "QUESTION_METRICS", indexes = {
        @Index(name = "VIEW_IDX", columnList = "COUNT_OF_VIEW"),
        @Index(name = "RECOMMEND_IDX", columnList = "COUNT_OF_RECOMMEND"),
        @Index(name = "EXPOSURE_IDX", columnList = "EXPOSURE")
})
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class QuestionMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_METRICS_ID")
    private Long id;
    @Column(name = "COUNT_OF_RECOMMEND")
    private Integer countOfRecommend;
    @Column(name = "COUNT_OF_VIEW")
    private Integer countOfView;
    @Enumerated(EnumType.STRING)
    @Column(name = "EXPOSURE")
    private ExposureStatus exposure;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "questionMetrics")
    private Question question;


    protected QuestionMetrics(Integer countOfRecommend, Integer countOfView, Question question) {
        this.countOfRecommend = countOfRecommend;
        this.countOfView = countOfView;
        this.exposure = question.getExposure();
        this.question = question;

    }

    public static QuestionMetrics createQuestionMetrics(Question question) {
        return new QuestionMetrics(0, 0, question);
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
