package com.study.badrequest.domain.member.dto;

import com.study.badrequest.domain.member.entity.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoDto {

    private Long id;
    private Authority authority;
}
