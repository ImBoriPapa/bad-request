package com.study.badrequest.login.command.domain;

import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemporaryPasswordRepository extends JpaRepository<TemporaryPassword,Long> {

    TemporaryPassword save(TemporaryPassword temporaryPassword);
    Optional<TemporaryPassword> findByMember(MemberEntity member);

}
