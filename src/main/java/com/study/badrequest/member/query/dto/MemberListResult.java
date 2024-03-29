package com.study.badrequest.member.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberListResult {
    private Long id;
    private String email;
    private String nickname;
    private String profileImagePath;

    public MemberListResult(Long id, String email, String nickname, String profileImagePath) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImagePath = profileImagePath;
    }
}
