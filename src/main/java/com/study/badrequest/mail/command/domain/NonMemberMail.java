package com.study.badrequest.mail.command.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NonMemberMail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "RECIPIENT_EMAIL")
    private String recipientEmail;
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
    @Builder(builderMethodName = "createNonMemberMail",access = AccessLevel.PROTECTED)
    protected NonMemberMail(String recipientEmail, String subject, String text, MailKind mailKind, Boolean sentMailSuccess, LocalDateTime timeOfSent) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.text = text;
        this.mailKind = mailKind;
        this.sentMailSuccess = true;
        this.timeOfSent = LocalDateTime.now();
    }

    public static NonMemberMail createAuthenticationMail(String recipientEmail, String subject){
        return NonMemberMail.createNonMemberMail()
                .recipientEmail(recipientEmail)
                .subject(subject)
                .mailKind(MailKind.EMAIL_AUTHENTICATION)
                .build();
    }
    public void sentFail(){
        this.sentMailSuccess = false;
    }

}
