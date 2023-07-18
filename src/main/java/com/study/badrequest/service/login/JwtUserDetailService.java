package com.study.badrequest.service.login;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.login.MemberPrincipal;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.exception.CustomRuntimeException;
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

    @Override
    public UserDetails loadUserByUsername(String changeableId) throws UsernameNotFoundException {
        log.info("Load User By Username");
        return memberRepository
                .findMemberByAuthenticationCodeAndDateIndex(changeableId, Member.getDateIndexInAuthenticationCode(changeableId))
                .map(member ->
                        new MemberPrincipal(
                                member.getId(),
                                member.getAuthenticationCode(),
                                member.getAuthority().getAuthorities())
                )
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
    }
}
