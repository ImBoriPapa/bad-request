package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByAuthenticationCodeAndDateIndex(String username, Long createDateTimeIndex);

    List<Member> findMembersByEmail(String email);
    List<Member> findMembersByContact(String contact);

}
