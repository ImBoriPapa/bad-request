package com.study.badrequest.domain.member.service;

import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.domain.member.dto.MemberResponse;
import com.study.badrequest.domain.member.entity.Authority;

/**
 *  Member Entity
 *  Create(signupMember),
 *  Update(changePermissions,updateContact,resetPassword),
 *  Delete(resignMember)
 */
public interface MemberCommandService {
    MemberResponse.SignupResult signupMember(MemberRequest.CreateMember form);

    void changePermissions(Long memberId, Authority authority);

    MemberResponse.UpdateResult updateContact(Long memberId, String contact);

    MemberResponse.UpdateResult resetPassword(Long id, String password, String newPassword);

    MemberResponse.DeleteResult resignMember(Long memberId, String password);
}
