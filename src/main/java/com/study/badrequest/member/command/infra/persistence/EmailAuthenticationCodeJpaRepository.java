package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.EmailAuthenticationCode;
import com.study.badrequest.member.command.domain.EmailAuthenticationCodeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthenticationCodeJpaRepository extends JpaRepository<EmailAuthenticationCode, Long>, EmailAuthenticationCodeRepository {

}
