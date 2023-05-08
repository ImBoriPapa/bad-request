package com.study.badrequest.domain.notification;


import com.study.badrequest.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_NOTIFICATION")
public class MemberNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_NOTIFICATION_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Member member;
    @Column(name = "MESSAGE")
    private String message;
    @Column(name = "IS_CHECKED")
    private Boolean isChecked;
    @Column(name = "LINK")
    private String link;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "createNotification")
    public MemberNotification(Member member, String message, Boolean isChecked, String link, LocalDateTime createdAt) {
        this.member = member;
        this.message = message;
        this.isChecked = isChecked;
        this.link = link;
        this.createdAt = createdAt;
    }
}
