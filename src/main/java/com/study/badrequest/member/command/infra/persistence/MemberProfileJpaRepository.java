package com.study.badrequest.member.command.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileJpaRepository extends JpaRepository<MemberProfileEntity,Long> {
}
