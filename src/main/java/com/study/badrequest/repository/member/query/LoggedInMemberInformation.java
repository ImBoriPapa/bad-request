package com.study.badrequest.repository.member.query;


import com.study.badrequest.domain.login.OauthProvider;
import com.study.badrequest.domain.member.Authority;
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
    private OauthProvider loggedInAs;
}
