package com.study.badrequest.domain.member.repository;


import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>,CustomMemberRepository {
    Optional<Member> findByEmail(String email);
    Optional<Member> findMemberByUsernameAndAuthority(String username, Authority authority);
    Optional<Member> findMemberByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByContact(String password);
}
