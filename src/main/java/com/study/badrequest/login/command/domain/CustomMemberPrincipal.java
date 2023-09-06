package com.study.badrequest.login.command.domain;

import com.study.badrequest.member.command.domain.values.Authority;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public final class CustomMemberPrincipal extends User {
    private final Long memberId;
    private final Authority authority;

    public CustomMemberPrincipal(Long id, Collection<? extends GrantedAuthority> authorities) {
        super(id.toString(), "", authorities);
        this.memberId = id;
        this.authority = Authority.getAuthorityByAuthorities(authorities);
    }
}


