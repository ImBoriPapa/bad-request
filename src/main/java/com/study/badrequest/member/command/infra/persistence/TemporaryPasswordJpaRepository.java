package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.TemporaryPassword;
import com.study.badrequest.member.command.domain.TemporaryPasswordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryPasswordJpaRepository extends JpaRepository<TemporaryPassword,Long>, TemporaryPasswordRepository {
}
