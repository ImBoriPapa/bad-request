package com.study.badrequest.login.command.domain;

import java.util.Optional;

public interface DisposalAuthenticationRepository {

    DisposableAuthenticationCode save(DisposableAuthenticationCode disposableAuthenticationCode);
    Optional<DisposableAuthenticationCode> findByCode(String code);
    void deleteById(Long id);
}
