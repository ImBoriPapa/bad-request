package com.study.badrequest.service.mail;


import com.study.badrequest.domain.member.AuthenticationMailInformation;

public interface NonMemberMailService {
    void sendAuthenticationMail(AuthenticationMailInformation authenticationMailInformation);
}
