package com.study.badrequest.domain.activity;

import com.study.badrequest.domain.member.Member;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "ACTIVITY")
@EqualsAndHashCode(of = "id")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVITY_ACTION")
    private ActivityAction action;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "RESOURCE_ID")
    private Long resourceId;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected Activity(Member member, ActivityAction action, String title, String description, Long resourceId, LocalDateTime createdAt) {
        this.member = member;
        this.action = action;
        this.title = title;
        this.description = description;
        this.resourceId = resourceId;
        this.createdAt = createdAt;
    }

    public static Activity postQuestion(Member member, String title, String description, Long questionId, LocalDateTime createdAt) {
        return new Activity(member, ActivityAction.QUESTION, title, description, questionId, createdAt);
    }
}