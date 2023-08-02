package com.study.badrequest.service.mail;


public interface MemberMailService {
    void sendWelcome(Long memberId);
    void sendTemporaryPassword(Long memberId, String temporaryPassword);


}
