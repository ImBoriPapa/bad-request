package com.study.badrequest.login.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.login.command.domain.MemberPrincipal;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.member.command.domain.repository.MemberRepository;

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
                .findMemberByAuthenticationCodeAndCreatedAt(changeableId, MemberEntity.getCreatedAtInAuthenticationCode(changeableId))
                .map(member ->
                        new MemberPrincipal(
                                member.getMemberId().getId(),
                                member.getAuthenticationCode(),
                                member.getAuthority().getAuthorities())
                )
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
    }
}
