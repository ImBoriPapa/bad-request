package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.TemporaryPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemporaryPasswordRepository extends JpaRepository<TemporaryPassword,Long> {
    Optional<TemporaryPassword> findByMember(Member member);

}
