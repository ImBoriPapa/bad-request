package com.study.badrequest.member.command.application;


import com.study.badrequest.member.command.application.dto.MemberWithDawnForm;

import java.time.LocalDateTime;


public interface MemberWithDrawnService {

    LocalDateTime withdrawalMember(MemberWithDawnForm form);

}
