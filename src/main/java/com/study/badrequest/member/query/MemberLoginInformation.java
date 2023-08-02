package com.study.badrequest.member.query;


import com.study.badrequest.member.command.domain.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberLoginInformation {
    private Long id;
    private String email;
    private String password;
    private String username;
    private Authority authority;
    private Boolean isConfirmedMail;
    private Boolean isTemporaryPassword;

    public MemberLoginInformation(Long id, String email, String password, String username, Authority authority,Boolean isConfirmedMail,Boolean isTemporaryPassword) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.authority = authority;
        this.isConfirmedMail = isConfirmedMail;
        this.isTemporaryPassword = isTemporaryPassword;
    }
}
