package com.study.badrequest.repository.login;

import com.study.badrequest.domain.member.AuthenticationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationCodeRepository extends JpaRepository<AuthenticationCode,Long> {
    Optional<AuthenticationCode> findByCode(String code);

    Optional<AuthenticationCode> findByEmail(String email);
}
