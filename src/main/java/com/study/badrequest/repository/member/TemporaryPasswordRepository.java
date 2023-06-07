package com.study.badrequest.repository.member;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.TemporaryPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemporaryPasswordRepository extends JpaRepository<TemporaryPassword,Long> {
    Optional<TemporaryPassword> findByMember(Member member);

}
