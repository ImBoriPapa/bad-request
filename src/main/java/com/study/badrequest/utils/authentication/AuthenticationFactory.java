package com.study.badrequest.utils.authentication;


import com.study.badrequest.login.command.domain.CustomMemberPrincipal;
import com.study.badrequest.member.command.domain.values.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
public class AuthenticationFactory {
    public static Authentication generateAuthentication(Long memberId, Authority authority) {
        log.info("Generate Authentication");
        Collection<? extends GrantedAuthority> authorities = authority.getAuthorities();

        return new UsernamePasswordAuthenticationToken(
                new CustomMemberPrincipal(memberId, authorities),
                "",
                authorities
        );
    }

}
