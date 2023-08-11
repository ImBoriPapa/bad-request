package com.study.badrequest.member.command.application;

public interface MemberAuthenticationService {

    EmailAuthenticationCodeValidityTime issueEmailAuthenticationCode(String email);
    Long issueTemporaryPassword(TemporaryPasswordIssuanceForm form);
}
