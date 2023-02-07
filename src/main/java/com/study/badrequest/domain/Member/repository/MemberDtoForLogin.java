package com.study.badrequest.domain.Member.repository;

import com.study.badrequest.domain.Member.entity.Member;
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
