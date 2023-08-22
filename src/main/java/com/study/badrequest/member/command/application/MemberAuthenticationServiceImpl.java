package com.study.badrequest.member.command.application;

import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.dto.MemberIssueTemporaryPassword;
import com.study.badrequest.member.command.domain.imports.MemberPasswordEncoder;
import com.study.badrequest.member.command.domain.imports.TemporaryPasswordGenerator;
import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.member.command.domain.values.AccountStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberAuthenticationServiceImpl implements MemberAuthenticationService {
    private final MemberRepository memberRepository;
    private final MemberPasswordEncoder memberPasswordEncoder;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;

    @Override
    @Transactional
    public Long issueTemporaryPassword(MemberIssueTemporaryPassword temporaryPassword) {

        List<Member> members = memberRepository.findMembersByEmail(temporaryPassword.email());

        Member member = getActiveOrUsingTemporaryMember(members);

        String generatedTemporaryPassword = temporaryPasswordGenerator.generator();
        Member issueTemporaryPassword = member.issueTemporaryPassword(generatedTemporaryPassword, memberPasswordEncoder);

        return memberRepository.save(issueTemporaryPassword).getMemberId().getId();
    }

    private Member getActiveOrUsingTemporaryMember(List<Member> members) {
        if (members.isEmpty()) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER);
        }

        Optional<Member> optionalMember = members.stream()
                .filter(member -> member.getAccountStatus() == ACTIVE || member.getAccountStatus() == USING_TEMPORARY_PASSWORD)
                .findFirst();


        if (optionalMember.isEmpty()) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER);
        }

        return optionalMember.get();
    }


}
