package com.study.badrequest.member.command.domain.dto;


import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.imports.AuthenticationCodeGenerator;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.model.MemberProfile;

public record CreateMemberByEmail(
        String email,
        String password,
        String contact,
        MemberProfile memberProfile,
        AuthenticationCodeGenerator authenticationCodeGenerator,
        MemberPasswordEncoder memberPasswordEncoder) {
    public CreateMemberByEmail {
        if (email == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.EMAIL_MUST_NOT_BE_NULL);
        }

        if (!email.contains("@")) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.WRONG_EMAIL_PATTERN);
        }

        if (password == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.PASSWORD_MUST_NOT_BE_NULL);
        }

        if(contact == null){
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.CONTACT_MUST_NOT_BE_NULL);
        }

        if (memberProfile == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NULL_MEMBER_PROFILE);
        }

        if (authenticationCodeGenerator == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NULL_AUTHENTICATION_CODE_GENERATOR);
        }

        if (memberPasswordEncoder == null) {
            throw CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NULL_MEMBER_PASSWORD_ENCODER);
        }
    }

}
