package com.study.badrequest.member.command.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class MemberWithDawnForm {
    private Long memberId;
    private String password;
    private String ipAddress;

}
