package com.study.badrequest.service.member;

import com.study.badrequest.dto.member.MemberRequestForm;
import com.study.badrequest.dto.member.MemberResponse;

/**
 * Member Entity
 * Create(signupMember),
 * Update(changePermissions,updateContact,resetPassword),
 * Delete(resignMember)
 */
public interface MemberCommandService {
    MemberResponse.Create signupMemberProcessing(MemberRequestForm.SignUp form);

    MemberResponse.SendAuthenticationEmail sendAuthenticationMailProcessing(String email);

    MemberResponse.Update updateContactProcessing(Long memberId, String contact);

    MemberResponse.Update changePasswordProcessing(Long memberId, MemberRequestForm.ChangePassword form);

    MemberResponse.TemporaryPassword issueTemporaryPasswordProcessing(String email);

    MemberResponse.Delete resignMemberProcessing(Long memberId, String password);

}
