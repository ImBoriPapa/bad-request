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
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected Activity(Member member, ActivityAction action, String title, String description, LocalDateTime createdAt) {
        this.member = member;
        this.action = action;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Activity createActivity(Member member, ActivityAction action, String title, String description, LocalDateTime createdAt) {
        Activity activity = new Activity(member, action, title, description, createdAt);
        activity.getMember().getMemberProfile().incrementActivityScore(action.getScore());
        return activity;
    }

}
