package com.study.badrequest.notification.command.application;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.notification.command.domain.NotificationMessage;
import com.study.badrequest.repository.notification.NotificationMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationMessageServiceImpl {

    private final NotificationMessageRepository notificationMessageRepository;

    @Transactional
    public void createWelcomeNotification(Member member) {
        NotificationMessage welcomeMessage = NotificationMessage.createWelcomeMessage(member);
        NotificationMessage save = notificationMessageRepository.save(welcomeMessage);

    }

    @Transactional
    public void createNotification(Member member, String message) {
        NotificationMessage noticeMessage = NotificationMessage.createNoticeMessage(member, message);
        NotificationMessage save = notificationMessageRepository.save(noticeMessage);
    }

    @Transactional
    public void createUpdateMessage(Member member, String message) {
        NotificationMessage updateMessage = NotificationMessage.createUpdateMessage(member, message);
        NotificationMessage save = notificationMessageRepository.save(updateMessage);
    }

    @Transactional
    public void createAnswerMessage(Member member, String title, String nickname) {
        NotificationMessage answerMessage = NotificationMessage.createAnswerMessage(member, title, nickname);
        NotificationMessage save = notificationMessageRepository.save(answerMessage);
    }

    @Transactional
    public void createCommentMessage(Member member, String answer, String nickname) {
        NotificationMessage commentMessage = NotificationMessage.createCommentMessage(member, answer, nickname);
        NotificationMessage save = notificationMessageRepository.save(commentMessage);
    }

    @Transactional
    public void createQuestionRecommendationMessage(Member member, String title, String nickname) {
        NotificationMessage questionRecommendationMessage = NotificationMessage.createQuestionRecommendationMessage(member, title, nickname);
        NotificationMessage save = notificationMessageRepository.save(questionRecommendationMessage);
    }

    @Transactional
    public void createAnswerRecommendationMessage(Member member, String answer, String nickname) {
        NotificationMessage answerRecommendationMessage = NotificationMessage.createAnswerRecommendationMessage(member, answer, nickname);
        NotificationMessage save = notificationMessageRepository.save(answerRecommendationMessage);
    }

    @Transactional
    public void markNotificationAsRead(Long memberId) {
        NotificationMessage notification = notificationMessageRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(""));
        notification.checkNotification();
    }


}
