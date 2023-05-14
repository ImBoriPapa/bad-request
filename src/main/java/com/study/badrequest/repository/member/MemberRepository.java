package com.study.badrequest.repository.member;


import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findMemberByChangeableIdAndCreateDateTimeIndex(String username, Long createDateTimeIndex);
    boolean existsByEmail(String email);
    boolean existsByContact(String password);


}
