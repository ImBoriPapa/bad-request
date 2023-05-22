package com.study.badrequest.repository.notification;

import com.study.badrequest.domain.notification.MemberNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberNotificationRepository extends JpaRepository<MemberNotification, Long> {
}
