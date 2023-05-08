package com.study.badrequest.utils.authentication;


import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.domain.member.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
public class AuthenticationFactory {

    public static Authentication generateAuthentication(String username, Long memberId, Authority authority) {
        log.info("Generate Authentication");
        Collection<? extends GrantedAuthority> authorities = authority.getAuthorities();

        return new UsernamePasswordAuthenticationToken(
                new CurrentLoggedInMember(username, memberId, authorities),
                "",
                authorities
        );
    }

}
