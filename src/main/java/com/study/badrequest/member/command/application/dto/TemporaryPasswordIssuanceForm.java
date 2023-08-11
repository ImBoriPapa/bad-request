package com.study.badrequest.member.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TemporaryPasswordIssuanceForm {
    private String email;
    private String ipAddress;
}
