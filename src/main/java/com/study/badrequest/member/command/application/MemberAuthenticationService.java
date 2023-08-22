package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.domain.dto.MemberIssueTemporaryPassword;

public interface MemberAuthenticationService {

    Long issueTemporaryPassword(MemberIssueTemporaryPassword temporaryPassword);
}
