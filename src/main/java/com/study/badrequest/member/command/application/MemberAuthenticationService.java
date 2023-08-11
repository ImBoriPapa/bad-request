package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.application.dto.TemporaryPasswordIssuanceForm;

public interface MemberAuthenticationService {

    EmailAuthenticationCodeValidityTime issueEmailAuthenticationCode(String email);
    Long issueTemporaryPassword(TemporaryPasswordIssuanceForm form);
}
