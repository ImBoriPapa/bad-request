package com.study.badrequest.member.command.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ContactChangeForm {

    private Long memberId;
    private String contact;
    private String ipAddress;
}
