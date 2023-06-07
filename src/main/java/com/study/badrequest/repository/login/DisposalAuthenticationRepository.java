package com.study.badrequest.repository.login;

import com.study.badrequest.domain.member.DisposalAuthenticationCode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisposalAuthenticationRepository extends JpaRepository<DisposalAuthenticationCode,Long> {
    Optional<DisposalAuthenticationCode> findByCode(String code);
}
