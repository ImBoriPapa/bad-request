package com.study.badrequest.admin.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.admin.command.domain.AdministratorActivityHistory;
import com.study.badrequest.member.command.domain.values.Authority;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.admin.command.domain.AdministratorActivityHistoryRepository;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
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

        MemberEntity admin = null;

        MemberEntity member = null;
        member.changePermissions(authority);

        AdministratorActivityHistory history = AdministratorActivityHistory.createAdministratorActivityHistory("", "", admin, member.getUpdatedAt());

        return administratorActivityHistoryRepository.save(history);
    }

}
