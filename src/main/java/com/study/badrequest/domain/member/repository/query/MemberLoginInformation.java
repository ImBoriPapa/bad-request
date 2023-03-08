package com.study.badrequest.domain.member.repository.query;

import com.study.badrequest.domain.member.entity.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberLoginInformation {
    private Long id;
    private String email;
    private String password;
    private String username;
    private Authority authority;

    public MemberLoginInformation(Long id, String email, String password, String username, Authority authority) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.authority = authority;
    }
}
