package com.study.badrequest.repository.notification;

import com.study.badrequest.domain.notification.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
}
