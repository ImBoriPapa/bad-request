package com.study.badrequest.service.login;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.MemberPrincipal;
import com.study.badrequest.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /**
     * Member.getUsername = String username == UUID
     */
    @Override
    @CustomLogTracer
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Load User By Username");
        return memberRepository.findByUsername(username)
                .map(member ->
                        new MemberPrincipal(
                                member.getId(),
                                member.getUsername(),
                                member.getAuthority().getAuthorities())
                )
                .orElseThrow(() -> new UsernameNotFoundException(ApiResponseStatus.NOTFOUND_MEMBER.getMessage()));
    }
}
