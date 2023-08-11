package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.application.dto.ContactChangeForm;
import com.study.badrequest.member.command.application.dto.PasswordChangeForm;

public interface MemberInformationUpdateService {

    Long changePassword(PasswordChangeForm form);

    Long changeContact(ContactChangeForm form);
}
