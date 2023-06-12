package com.study.badrequest.repository.login;

import com.study.badrequest.domain.member.DisposableAuthenticationCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisposalAuthenticationRepository extends JpaRepository<DisposableAuthenticationCode,Long> {
    Optional<DisposableAuthenticationCode> findByCode(String code);
}
