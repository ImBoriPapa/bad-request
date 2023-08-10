package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long>, MemberRepository {

}
