package com.study.badrequest.domain.member.dto;

import com.study.badrequest.domain.member.entity.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {
    private String username;
    private String password;
    private Authority authority;

    public MemberDto(String username, String password, Authority authority) {
        this.username = username;
        this.password = password;
        this.authority = authority;
    }
}