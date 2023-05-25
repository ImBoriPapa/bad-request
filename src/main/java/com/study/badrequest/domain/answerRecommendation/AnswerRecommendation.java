package com.study.badrequest.domain.answerRecommendation;

import com.study.badrequest.domain.answer.Answer;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.recommendation.RecommendationKind;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    public AnswerRecommendation(RecommendationKind kind, Member member, Answer answer) {
        this.kind = kind;
        this.member = member;
        this.answer = answer;
    }
}
