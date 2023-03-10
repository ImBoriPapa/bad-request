package com.study.badrequest.domain.member.repository.query;

import com.study.badrequest.domain.member.entity.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberAuthDto {

    private Long id;
    private Authority authority;
}
