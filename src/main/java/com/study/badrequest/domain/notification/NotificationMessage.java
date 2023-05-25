package com.study.badrequest.domain.notification;


import com.study.badrequest.domain.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.study.badrequest.domain.notification.NotificationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_NOTIFICATION")
@EqualsAndHashCode(of = "id")
public class NotificationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_NOTIFICATION_ID")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Member member;
    @Column(name = "MESSAGE")
    private String message;
    @Column(name = "IS_READ")
    private Boolean isRead;
    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    protected NotificationMessage(Member member, String message, Boolean isRead, NotificationType type, LocalDateTime createdAt) {
        this.member = member;
        this.message = message;
        this.isRead = isRead;
        this.type = type;
        this.createdAt = createdAt;
    }

    private static NotificationMessage createMessage(Member member, String result, NotificationType notice) {
        return new NotificationMessage(member, result, false, notice, LocalDateTime.now());
    }

    public static NotificationMessage createWelcomeMessage(Member member) {
        String welcome = "Welcome to Bad-Request !!!";
        return new NotificationMessage(member, welcome, false, WELCOME, LocalDateTime.now());
    }

    public static NotificationMessage createNoticeMessage(Member member, String message) {
        String result = "[공지사항] " + message;
        return createMessage(member, result, NOTICE);
    }

    public static NotificationMessage createUpdateMessage(Member member, String message) {

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 W주차");
        String formattedDate = now.format(formatter);

        String result = String.format("[업데이트 소식] %s - %s", formattedDate, message);

        return createMessage(member, result, UPDATE);
    }

    public static NotificationMessage createAnswerMessage(Member member, String questionTitle, String nickname) {
        String result = "회원님의 질문 \"" + questionTitle + "\" 에 " + nickname + " 님이 답변을 달았습니다.";

        return createMessage(member, result, ANSWER);
    }

    public static NotificationMessage createCommentMessage(Member member, String answer, String nickname) {
        String result = "회원님의 답변 \"" + answer + "\" 에 " + nickname + " 님이 댓글을 달았습니다.";

        return createMessage(member, result, ADD_COMMENT);
    }

    public static NotificationMessage createQuestionRecommendationMessage(Member member, String questionTitle, String nickname) {
        String result = "회원님의 질문 \"" + questionTitle + "\" 을 " + nickname + " 님이 추천 하였습니다.";

        return createMessage(member, result, QUESTION_RECOMMENDATION);
    }

    public static NotificationMessage createAnswerRecommendationMessage(Member member, String answer, String nickname) {
        String result = "회원님의 답변 \"" + answer + "\" 을 " + nickname + " 님이 추천 하였습니다.";

        return createMessage(member, result, ANSWER_RECOMMENDATION);
    }


    public void checkNotification() {
        this.isRead = true;
    }
}
