package com.study.badrequest.domain.recommendation;


import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "RECOMMENDATION")
@EqualsAndHashCode(of = "id")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(name = "KIND")
    private RecommendationKind kind;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected Recommendation(Member member, Question question, RecommendationKind kind) {
        this.member = member;
        this.question = question;
        this.kind = kind;
        this.createdAt = LocalDateTime.now();
    }

    public static Recommendation createRecommendation(Member member, Question question, RecommendationKind kind) {

        Recommendation recommendation = new Recommendation(member, question, kind);

        if (kind == RecommendationKind.RECOMMENDATION) {
            recommendation.changeToRecommendation();
        } else {
            recommendation.changeToUnRecommendation();
        }

        return recommendation;
    }

    public void changeToRecommendation() {
        this.question.getQuestionMetrics().incrementCountOfRecommendations();
        this.kind = RecommendationKind.RECOMMENDATION;
    }

    public void changeToUnRecommendation() {
        this.question.getQuestionMetrics().decrementCountOfRecommendations();
        this.kind = RecommendationKind.UN_RECOMMENDATION;
    }

}
