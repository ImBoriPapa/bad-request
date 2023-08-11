package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.DisposableAuthenticationCode;
import com.study.badrequest.member.command.domain.DisposalAuthenticationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DisposalAuthenticationJpaRepository extends JpaRepository<DisposableAuthenticationCode, Long>, DisposalAuthenticationRepository {
    Optional<DisposableAuthenticationCode> findByCode(String code);
}
