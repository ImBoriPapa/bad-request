package com.study.badrequest.repository.member.query;


import com.study.badrequest.domain.login.OauthProvider;
import com.study.badrequest.domain.member.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberDetailDto {
    private Long id;
    private String email;
    private String contact;
    private String nickname;
    private String selfIntroduce;
    private String profileImage;
    private Authority authority;
    private OauthProvider loginType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
