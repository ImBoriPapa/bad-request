package com.study.badrequest.service.admin;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.admin.AdministratorActivityHistory;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.admin.AdministratorActivityHistoryRepository;
import com.study.badrequest.repository.member.MemberRepository;
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

        Member admin = memberRepository.findById(adminId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));
        member.changePermissions(authority);

        AdministratorActivityHistory history = AdministratorActivityHistory.createAdministratorActivityHistory("", "", admin, member.getUpdatedAt());

        return administratorActivityHistoryRepository.save(history);
    }

}
