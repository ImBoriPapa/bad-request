package com.study.badrequest.repository.member;

import com.study.badrequest.domain.member.EmailAuthenticationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthenticationCodeRepository extends JpaRepository<EmailAuthenticationCode,Long> {
    Optional<EmailAuthenticationCode> findByEmail(String email);
}
