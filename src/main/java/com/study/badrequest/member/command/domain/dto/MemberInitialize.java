package com.study.badrequest.member.command.domain.dto;

import com.study.badrequest.member.command.domain.model.MemberPassword;
import com.study.badrequest.member.command.domain.model.MemberProfile;
import com.study.badrequest.member.command.domain.values.AccountStatus;
import com.study.badrequest.member.command.domain.values.Authority;
import com.study.badrequest.member.command.domain.values.RegistrationType;
import lombok.Builder;


import java.time.LocalDateTime;

public record MemberInitialize(Long memberId,
                               String authenticationCode,
                               String oauthId,
                               String memberEmail,
                               MemberProfile memberProfile,
                               RegistrationType registrationType,
                               MemberPassword memberPassword,
                               String contact,
                               Authority authority,
                               AccountStatus accountStatus,
                               LocalDateTime signInAt,
                               LocalDateTime updatedAt,
                               LocalDateTime resignAt) {
    @Builder
    public MemberInitialize {
    }
}
