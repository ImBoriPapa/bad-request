package com.study.badrequest.domain.Member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberForm {
    private String password;
    private String newPassword;
    private String contact;
}
