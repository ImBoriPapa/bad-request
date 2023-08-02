package com.study.badrequest.admin.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.admin.command.domain.AdministratorActivityHistory;
import com.study.badrequest.member.command.domain.Authority;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.admin.command.domain.AdministratorActivityHistoryRepository;
import com.study.badrequest.member.command.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberManagementServiceImpl implements MemberManagementService {
    private final MemberRepository memberRepository;
    private final AdministratorActivityHistoryRepository administratorActivityHistoryRepository;

    @Transactional
    public AdministratorActivityHistory changeMemberAuthority(Long adminId, Long memberId, Authority authority, String reason) {

        Member admin = memberRepository.findById(adminId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));

        Member member = memberRepository.findById(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOTFOUND_MEMBER));
        member.changePermissions(authority);

        AdministratorActivityHistory history = AdministratorActivityHistory.createAdministratorActivityHistory("", "", admin, member.getUpdatedAt());

        return administratorActivityHistoryRepository.save(history);
    }

}