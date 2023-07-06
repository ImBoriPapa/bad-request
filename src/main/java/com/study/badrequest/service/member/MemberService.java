package com.study.badrequest.service.member;

import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;

/**
 * Member Entity
 * Create(signupMember),
 * Update(changePermissions,updateContact,resetPassword),
 * Delete(resignMember)
 */
public interface MemberService {
    MemberResponse.Create signupMemberProcessingByEmail(MemberRequest.SignUp form, String ipAddress);

    MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String email);

    MemberResponse.Update changeContactProcessing(Long memberId, String contact, String ipAddress);

    MemberResponse.Update changePasswordProcessing(Long memberId, MemberRequest.ChangePassword form, String ipAddress);

    MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String email,String ipAddress);

    MemberResponse.Delete withdrawalMemberProcessing(Long memberId, String password, String ipAddress);

}
