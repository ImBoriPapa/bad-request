package com.study.badrequest.domain.member.repository.query;

import com.study.badrequest.domain.member.entity.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSimpleInformation {
    private Long id;
    private String username;
    private Authority authority;

}
