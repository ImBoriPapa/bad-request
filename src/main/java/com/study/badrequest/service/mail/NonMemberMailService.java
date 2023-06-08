package com.study.badrequest.service.mail;



import com.study.badrequest.domain.member.EmailAuthenticationCode;


public interface NonMemberMailService {
    void sendAuthenticationMail(String email,String code);
}
