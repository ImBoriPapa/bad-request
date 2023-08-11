package com.study.badrequest.member.command.domain;

import com.study.badrequest.common.exception.CustomRuntimeException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static com.study.badrequest.common.response.ApiResponseStatus.INVALID_EMAIL_FORM;

@Embeddable
@NoArgsConstructor
@Getter
public class MemberEmail {
    @Column(name = "email", nullable = false)
    private String email;

    protected MemberEmail(String email) {
        this.email = email;
    }

    public static MemberEmail createMemberEmail(String email) {
        String convertedEmail = convertDomainToLowercase(email);
        return new MemberEmail(convertedEmail);
    }

    public static String convertDomainToLowercase(String email) {
        int index = email.lastIndexOf('@');

        if (index != -1) {
            String localPart = email.substring(0, index);
            String domainPart = email.substring(index + 1);
            String convertedDomainPart = domainPart.toLowerCase();
            return localPart + "@" + convertedDomainPart;
        }

        throw CustomRuntimeException.createWithApiResponseStatus(INVALID_EMAIL_FORM);
    }
}
