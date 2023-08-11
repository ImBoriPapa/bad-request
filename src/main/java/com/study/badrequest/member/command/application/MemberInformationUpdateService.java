package com.study.badrequest.member.command.application;

public interface MemberInformationUpdateService {

    Long changePassword(PasswordChangeForm form);

    Long changeContact(ContactChangeForm form);
}
