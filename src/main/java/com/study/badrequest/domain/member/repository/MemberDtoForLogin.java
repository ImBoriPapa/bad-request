package com.study.badrequest.domain.member.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDtoForLogin {
    private Long id;
    private String email;
    private String username;
    private String password;

}
