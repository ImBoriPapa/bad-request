package com.study.badrequest.mail.command.application;


public interface NonMemberMailService {
    void sendAuthenticationMail(String email,String code);
}
