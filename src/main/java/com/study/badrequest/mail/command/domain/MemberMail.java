package com.study.badrequest.mail.command.domain;


import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_mail", indexes = {
        @Index(name = "MEMBER_SERVICE_MAIL_KIND_IDX", columnList = "MAIL_KIND")
})
@EqualsAndHashCode(of = "id")
public class MemberMail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_MAIL_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private MemberEntity recipient;
    @Column(name = "SUBJECT")
    private String subject;
    @Column(name = "TEXT")
    private String text;
    @Column(name = "MAIL_KIND")
    @Enumerated(EnumType.STRING)
    private MailKind mailKind;
    @Column(name = "SENT_SUCCESS")
    private Boolean sentMailSuccess;
    @Column(name = "TIME_OF_SENT")
    private LocalDateTime timeOfSent;

    @Builder(builderMethodName = "createMemberMail", access = AccessLevel.PROTECTED)
    protected MemberMail(MemberEntity recipient, String subject, String text, MailKind mailKind) {
        this.recipient = recipient;
        this.subject = subject;
        this.text = text;
        this.mailKind = mailKind;
        this.sentMailSuccess = true;
        this.timeOfSent = LocalDateTime.now();
    }

    public static MemberMail createTemporaryPasswordMail(MemberEntity recipient, String subject) {
        return MemberMail.createMemberMail()
                .recipient(recipient)
                .subject(subject)
                .mailKind(MailKind.PROVISIONAL_PASSWORD_ISSUE)
                .build();
    }

    public void sentFail() {
        this.sentMailSuccess = false;
    }
}
