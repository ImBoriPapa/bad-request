package com.study.badrequest.member.command.domain;


import com.study.badrequest.member.command.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CurrentMember extends User {
    private final Information information;

    public CurrentMember(String username, Long id, Collection<? extends GrantedAuthority> authorities) {
        super(username, "", authorities);
        this.information = new Information(id, username, Authority.getAuthorityByAuthorities(authorities));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Information {
        private Long id;
        private String username;
        private Authority authority;
    }
}


