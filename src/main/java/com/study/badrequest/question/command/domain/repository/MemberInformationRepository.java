package com.study.badrequest.question.command.domain.repository;



import com.study.badrequest.question.command.domain.dto.MemberInformation;

import java.util.Optional;

public interface MemberInformationRepository {
    Optional<MemberInformation> findByMemberId(Long memberId);
}
