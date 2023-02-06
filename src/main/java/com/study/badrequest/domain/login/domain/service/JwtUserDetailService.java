package com.study.badrequest.domain.login.domain.service;

import com.study.badrequest.domain.Member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    // TODO: 2023/01/02 Query 최적화

    /**
     * Member.getUsername = email
     *
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("[UserDetailService.loadUserByUsername]");

        return memberRepository.findByUsername(username)
                .map(member -> new User(
                        member.getUsername(),
                        member.getPassword(),
                        member.getAuthorities()))
                .orElseThrow(() -> new UsernameNotFoundException(CustomStatus.NOTFOUND_MEMBER.getMessage()));

    }
}
