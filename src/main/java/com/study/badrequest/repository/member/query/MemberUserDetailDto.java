package com.study.badrequest.repository.member.query;


import com.study.badrequest.domain.member.Authority;
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