package com.study.badrequest.member.command.application;


import java.time.LocalDateTime;


public interface MemberWithDrawnService {

    LocalDateTime withdrawalMemberProcessing(MemberWithDawnForm form);

}
