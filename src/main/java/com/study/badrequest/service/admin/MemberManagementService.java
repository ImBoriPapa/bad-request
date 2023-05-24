package com.study.badrequest.service.admin;

import com.study.badrequest.domain.admin.AdministratorActivityHistory;
import com.study.badrequest.domain.member.Authority;

public interface MemberManagementService {
     AdministratorActivityHistory changeMemberAuthority(Long adminId, Long memberId, Authority authority, String reason);
}
