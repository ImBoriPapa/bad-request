package com.study.badrequest.active.command.domain;

import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "activity")
@EqualsAndHashCode(of = "id")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity member;
    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVITY_ACTION")
    private ActivityAction action;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected Activity(MemberEntity member, ActivityAction action, String title, String description, LocalDateTime createdAt) {
        this.member = member;
        this.action = action;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Activity createActivity(MemberEntity member, ActivityAction action, String title, String description, LocalDateTime createdAt) {
        return new Activity(member, action, title, description, createdAt);
    }

}
