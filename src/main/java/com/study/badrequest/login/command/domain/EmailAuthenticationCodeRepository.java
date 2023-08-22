package com.study.badrequest.login.command.domain;

import java.util.Optional;

public interface EmailAuthenticationCodeRepository  {

    EmailAuthenticationCode save(EmailAuthenticationCode emailAuthenticationCode);
    Optional<EmailAuthenticationCode> findByEmail(String email);

    void delete(EmailAuthenticationCode emailAuthenticationCode);
}
