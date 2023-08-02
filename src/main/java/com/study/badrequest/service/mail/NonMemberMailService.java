package com.study.badrequest.service.mail;


public interface NonMemberMailService {
    void sendAuthenticationMail(String email,String code);
}
