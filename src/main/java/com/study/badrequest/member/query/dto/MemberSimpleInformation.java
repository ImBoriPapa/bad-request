package com.study.badrequest.member.query.dto;


import com.study.badrequest.member.command.domain.values.Authority;
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
