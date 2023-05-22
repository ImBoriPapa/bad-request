package com.study.badrequest.domain.notification;


import com.study.badrequest.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_NOTIFICATION")
@EqualsAndHashCode(of = "id")
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
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "createNotification")
    public MemberNotification(Member member, String message, Boolean isChecked, LocalDateTime createdAt) {
        this.member = member;
        this.message = message;
        this.isChecked = isChecked;
        this.createdAt = createdAt;
    }

    public void checkNotification(){
        this.isChecked = true;
    }
}
