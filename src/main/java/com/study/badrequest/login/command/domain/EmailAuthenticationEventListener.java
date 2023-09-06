package com.study.badrequest.login.command.domain;

import com.study.badrequest.mail.command.application.NonMemberMailService;
import com.study.badrequest.member.command.domain.events.MemberEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAuthenticationEventListener {
    private final NonMemberMailService nonMemberMailService;

    public void handleEvent(MemberEventDto.SendAuthenticationMail dto){
        
    }
}
