package com.study.badrequest.recommandation.command.domain;


import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.question.command.infra.persistence.question.QuestionEntity;
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
    private QuestionEntity question;

    @Enumerated(EnumType.STRING)
    @Column(name = "KIND")
    private RecommendationKind kind;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected QuestionRecommendation(MemberEntity member, QuestionEntity question, RecommendationKind kind) {
        this.member = member;
        this.question = question;
        this.kind = kind;
        this.createdAt = LocalDateTime.now();
    }




}
