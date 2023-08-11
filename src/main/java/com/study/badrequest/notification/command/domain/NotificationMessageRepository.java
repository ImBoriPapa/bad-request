package com.study.badrequest.notification.command.domain;


import java.util.Optional;

public interface NotificationMessageRepository  {

    NotificationMessage save(NotificationMessage notificationMessage);

    Optional<NotificationMessage> findById(Long id);
}
