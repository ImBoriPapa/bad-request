package com.study.badrequest.service.mail;


import com.study.badrequest.domain.member.AuthenticationCode;


public interface NonMemberMailService {
    void sendAuthenticationMail(AuthenticationCode authenticationCode);
}
