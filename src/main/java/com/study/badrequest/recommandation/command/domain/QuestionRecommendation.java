package com.study.badrequest.recommandation.command.domain;


import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.question.command.domain.Question;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "question_recommendation")
@EqualsAndHashCode(of = "id")
public class QuestionRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(name = "KIND")
    private RecommendationKind kind;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected QuestionRecommendation(MemberEntity member, Question question, RecommendationKind kind) {
        this.member = member;
        this.question = question;
        this.kind = kind;
        this.createdAt = LocalDateTime.now();
    }

    public static QuestionRecommendation createRecommendation(MemberEntity member, Question question, RecommendationKind kind) {

        QuestionRecommendation questionRecommendation = new QuestionRecommendation(member, question, kind);

        if (kind == RecommendationKind.RECOMMENDATION) {
            questionRecommendation.changeToRecommendation();
        } else if (kind == RecommendationKind.UN_RECOMMENDATION) {
            questionRecommendation.changeToUnRecommendation();
        }

        return questionRecommendation;
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
