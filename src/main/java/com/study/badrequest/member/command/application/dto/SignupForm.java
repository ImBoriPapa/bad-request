package com.study.badrequest.member.command.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignupForm {
    private String email;
    private String password;
    private String nickname;
    private String contact;
    private String authenticationCode;
    private String ipAddress;

    @Builder
    public SignupForm(String email, String password, String nickname, String contact, String authenticationCode, String ipAddress) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.contact = contact;
        this.authenticationCode = authenticationCode;
        this.ipAddress = ipAddress;
    }
}
