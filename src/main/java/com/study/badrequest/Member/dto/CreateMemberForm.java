package com.study.badrequest.Member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class CreateMemberForm {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String contact;

    @Builder

    public CreateMemberForm(String email, String password, String name, String nickname, String contact) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.contact = contact;
    }
}
