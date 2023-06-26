package com.study.badrequest.event.member;

import com.study.badrequest.service.mail.NonMemberMailService;
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
