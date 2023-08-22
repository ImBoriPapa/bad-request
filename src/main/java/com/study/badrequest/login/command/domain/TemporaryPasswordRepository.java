package com.study.badrequest.login.command.domain;

import com.study.badrequest.member.command.infra.persistence.MemberEntity;

import java.util.Optional;

public interface TemporaryPasswordRepository  {

    TemporaryPassword save(TemporaryPassword temporaryPassword);
    Optional<TemporaryPassword> findByMember(MemberEntity member);

}
