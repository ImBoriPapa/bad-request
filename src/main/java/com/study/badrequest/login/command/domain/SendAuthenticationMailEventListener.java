package com.study.badrequest.login.command.domain;

import com.study.badrequest.mail.command.application.NonMemberMailService;
import com.study.badrequest.member.command.domain.events.MemberEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.study.badrequest.config.AsyncConfig.WELCOME_MAIL_ASYNC_EXECUTOR;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendAuthenticationMailEventListener {

    private final NonMemberMailService nonMemberMailService;

    @Async(WELCOME_MAIL_ASYNC_EXECUTOR)
    @EventListener
    public void handleSendAuthenticationEmail(MemberEventDto.SendAuthenticationMail dto) {
        log.info("Send Authentication Email Event");
        nonMemberMailService.sendAuthenticationMail(dto.getEmail(), dto.getCode());
    }

}
