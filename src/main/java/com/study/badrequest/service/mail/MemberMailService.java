package com.study.badrequest.service.mail;


import com.study.badrequest.domain.member.Member;

public interface MemberMailService {

    void sendTemporaryPassword(Long memberId, String temporaryPassword);


}
