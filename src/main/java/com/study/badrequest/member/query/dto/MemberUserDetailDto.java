package com.study.badrequest.member.query.dto;


import com.study.badrequest.member.command.domain.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUserDetailDto {
    private String username;
    private String password;
    private Authority authority;

    public MemberUserDetailDto(String username, String password, Authority authority) {
        this.username = username;
        this.password = password;
        this.authority = authority;
    }
}