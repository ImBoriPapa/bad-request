package com.study.badrequest.member.query;


import com.study.badrequest.member.command.domain.RegistrationType;
import com.study.badrequest.member.command.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoggedInMemberInformation {
    private Long id;
    private Authority authority;
    private String nickname;
    private String profileImage;
    private RegistrationType loggedInAs;
}
