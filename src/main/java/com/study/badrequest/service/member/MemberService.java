package com.study.badrequest.service.member;

import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;

/**
 * Member Entity
 * Create(signupMember),
 * Update(changePermissions,updateContact,resetPassword),
 * Delete(resignMember)
 */
public interface MemberService {
    MemberResponse.Create signupMemberProcessingByEmail(MemberRequestForm.SignUp form, String ipAddress);

    MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String email);

    MemberResponse.Update changeContactProcessing(Long memberId, String contact, String ipAddress);

    MemberResponse.Update changePasswordProcessing(Long memberId, MemberRequestForm.ChangePassword form,String ipAddress);

    MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String email,String ipAddress);

    MemberResponse.Delete resignMemberProcessing(Long memberId, String password,String ipAddress);

}
