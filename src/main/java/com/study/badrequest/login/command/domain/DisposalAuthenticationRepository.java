package com.study.badrequest.login.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisposalAuthenticationRepository extends JpaRepository<DisposableAuthenticationCode,Long> {

    DisposableAuthenticationCode save(DisposableAuthenticationCode disposableAuthenticationCode);
    Optional<DisposableAuthenticationCode> findByCode(String code);
    void deleteById(Long id);
}
