package com.study.badrequest.mail.command.application;


public interface MemberMailService {
    void sendWelcome(Long memberId);
    void sendTemporaryPassword(Long memberId, String temporaryPassword);


}
