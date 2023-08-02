package com.study.badrequest.answer.command.domain;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.recommandation.command.domain.RecommendationKind;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ANSWER_RECOMMENDATION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id")
public class AnswerRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANSWER_RECOMMENDATION_ID")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "RECOMMENDATION_KIND")
    private RecommendationKind kind;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected AnswerRecommendation(Member member, Answer answer, RecommendationKind kind) {
        this.kind = kind;
        this.member = member;
        this.answer = answer;
        this.createdAt = LocalDateTime.now();
    }

    public static AnswerRecommendation createRecommendation(Member member, Answer answer, RecommendationKind kind) {

        AnswerRecommendation recommendation = new AnswerRecommendation(member, answer, kind);

        if (kind == RecommendationKind.RECOMMENDATION) {
            recommendation.getAnswer().incrementRecommendation();
        } else if (kind == RecommendationKind.UN_RECOMMENDATION) {
            recommendation.getAnswer().decrementRecommendation();
        }
        return recommendation;
    }
}
