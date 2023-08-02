package com.study.badrequest.admin.command.application;

import com.study.badrequest.admin.command.domain.AdministratorActivityHistory;
import com.study.badrequest.member.command.domain.Authority;

public interface MemberManagementService {
     AdministratorActivityHistory changeMemberAuthority(Long adminId, Long memberId, Authority authority, String reason);
}
