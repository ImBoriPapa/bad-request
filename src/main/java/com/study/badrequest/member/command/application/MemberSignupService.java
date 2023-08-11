package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.application.dto.SignupForm;

public interface MemberSignupService {
    Long signupByEmail(SignupForm form);

}
