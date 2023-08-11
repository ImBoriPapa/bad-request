package com.study.badrequest.member.command.domain;

import com.study.badrequest.member.command.domain.EmailAuthenticationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthenticationCodeRepository  {

    EmailAuthenticationCode save(EmailAuthenticationCode emailAuthenticationCode);
    Optional<EmailAuthenticationCode> findByEmail(String email);

    void delete(EmailAuthenticationCode emailAuthenticationCode);
}
