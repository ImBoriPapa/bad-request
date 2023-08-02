package com.study.badrequest.notification.command.domain;

import com.study.badrequest.notification.command.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
}
