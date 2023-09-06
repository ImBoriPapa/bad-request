package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.application.dto.MemberCreateForm;
import com.study.badrequest.member.command.domain.dto.MemberChangeContact;
import com.study.badrequest.member.command.domain.dto.MemberChangePassword;
import com.study.badrequest.member.command.domain.values.MemberId;
import com.study.badrequest.member.command.domain.dto.MemberResign;

import java.time.LocalDateTime;

public interface MemberManagementService {
    MemberId signupByEmail(MemberCreateForm memberCreateForm);
    MemberId changePassword(MemberId memberId, MemberChangePassword memberChangePassword);
    MemberId changeContact(MemberId memberId, MemberChangeContact memberChangeContact);
    LocalDateTime resign(MemberId memberId, MemberResign memberResign);
}
