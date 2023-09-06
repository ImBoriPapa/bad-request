package com.study.badrequest.member.command.domain.values;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import static com.study.badrequest.utils.email.EmailFormatter.convertDomainToLowercase;

@Getter
@EqualsAndHashCode(of = "email")
public final class MemberEmail {

    private final String email;

    private MemberEmail(String email) {
        this.email = email;
    }

    public static MemberEmail createMemberEmail(String email) {
        return new MemberEmail(convertDomainToLowercase(email));
    }


}
