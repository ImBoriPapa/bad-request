package com.study.badrequest.domain.login.service;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.domain.member.repository.MemberQueryRepository;

import com.study.badrequest.commons.consts.CustomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailService implements UserDetailsService {
    private final MemberQueryRepository memberQueryRepositoryImpl;

    /**
     * Member.getUsername = email
     *
     * @throws UsernameNotFoundException
     */

    @Override
    @CustomLogTracer
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberQueryRepositoryImpl.findByUsername(username)
                .map(memberDto -> new User(
                                memberDto.getUsername(),
                                memberDto.getPassword(),
                                memberDto.getAuthority().getAuthorities()
                        )
                ).orElseThrow(() -> new UsernameNotFoundException(CustomStatus.NOTFOUND_MEMBER.getMessage()));
    }
}
