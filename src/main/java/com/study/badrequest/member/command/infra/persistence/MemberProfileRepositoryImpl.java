package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.repository.MemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberProfileRepositoryImpl implements MemberProfileRepository {
    private final MemberProfileJpaRepository memberProfileJpaRepository;
}
