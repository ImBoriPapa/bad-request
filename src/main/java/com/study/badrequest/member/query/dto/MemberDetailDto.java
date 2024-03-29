package com.study.badrequest.member.query.dto;


import com.study.badrequest.member.command.domain.values.RegistrationType;
import com.study.badrequest.member.command.domain.values.Authority;
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
    private RegistrationType loginType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
