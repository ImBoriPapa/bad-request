package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.EmailAuthenticationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthenticationCodeRepository extends JpaRepository<EmailAuthenticationCode,Long> {
    Optional<EmailAuthenticationCode> findByEmail(String email);
}
