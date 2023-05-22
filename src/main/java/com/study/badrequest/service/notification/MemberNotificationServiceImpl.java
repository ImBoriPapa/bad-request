package com.study.badrequest.service.notification;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.notification.MemberNotification;
import com.study.badrequest.repository.notification.MemberNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberNotificationServiceImpl {

    private final MemberNotificationRepository memberNotificationRepository;

    @Transactional
    public void createNotification(Member member, String message) {
        MemberNotification notification = MemberNotification.createNotification()
                .member(member)
                .message(message)
                .isChecked(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void checkNotification(Long memberId) {
        MemberNotification notification = memberNotificationRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException(""));
        notification.checkNotification();
    }
}
