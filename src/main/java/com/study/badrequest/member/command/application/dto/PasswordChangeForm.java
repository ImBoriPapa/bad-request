package com.study.badrequest.member.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeForm {
    private Long memberId;
    private String currentPassword;
    private String newPassword;
    private String ipAddress;
}
