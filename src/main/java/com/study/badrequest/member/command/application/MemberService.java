package com.study.badrequest.member.command.application;

import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;

/**
 * Member Entity
 * Create(signupMember),
 * Update(changePermissions,updateContact,resetPassword),
 * Delete(resignMember)
 */
public interface MemberService {
    Long signUpWithEmail(SignUpForm form);

    MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String email);

    MemberResponse.Update changeContactProcessing(Long memberId, String contact, String ipAddress);

    MemberResponse.Update changePasswordProcessing(Long memberId, MemberRequest.ChangePassword form, String ipAddress);

    MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String email,String ipAddress);

    MemberResponse.Delete withdrawalMemberProcessing(Long memberId, String password, String ipAddress);

}