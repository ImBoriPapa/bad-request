package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.DisposableAuthenticationCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisposalAuthenticationRepository extends JpaRepository<DisposableAuthenticationCode,Long> {
    Optional<DisposableAuthenticationCode> findByCode(String code);
}
