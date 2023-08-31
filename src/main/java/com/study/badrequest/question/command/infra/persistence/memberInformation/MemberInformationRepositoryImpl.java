package com.study.badrequest.question.command.infra.persistence.memberInformation;

import com.study.badrequest.member.command.infra.persistence.MemberJpaRepository;
import com.study.badrequest.question.command.domain.dto.MemberInformation;
import com.study.badrequest.question.command.domain.repository.MemberInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;


@Repository
@Transactional
@RequiredArgsConstructor
public class MemberInformationRepositoryImpl implements MemberInformationRepository {
    private final MemberJpaRepository memberJpaRepository;

    // TODO: 2023/08/25 쿼리 최적화
    @Override
    public Optional<MemberInformation> findByMemberId(Long id) {
        return memberJpaRepository.findById(id).map(member -> new MemberInformation(
                member.getId(),
                member.getMemberProfile().getNickname(),
                member.getMemberProfile().getProfileImage().getImageLocation(),
                member.getMemberProfile().getActivityScore(),
                member.getAuthority()
        ));
    }

}
