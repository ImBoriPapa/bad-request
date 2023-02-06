package com.study.badrequest.domain.Member.repository;


import com.study.badrequest.domain.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByContact(String password);
}