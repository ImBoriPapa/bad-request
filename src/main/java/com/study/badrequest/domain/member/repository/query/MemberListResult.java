package com.study.badrequest.domain.member.repository.query;

import com.study.badrequest.domain.member.entity.Member;
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
