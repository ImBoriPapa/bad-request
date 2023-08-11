package com.study.badrequest.notification.command.infra.persistence;

import com.study.badrequest.notification.command.domain.NotificationMessage;
import com.study.badrequest.notification.command.domain.NotificationMessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMessageJpaRepository extends JpaRepository<NotificationMessage, Long>, NotificationMessageRepository {
}
