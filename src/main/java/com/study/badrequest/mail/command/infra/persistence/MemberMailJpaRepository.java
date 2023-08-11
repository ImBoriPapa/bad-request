package com.study.badrequest.mail.command.infra.persistence;

import com.study.badrequest.mail.command.domain.MemberMail;
import com.study.badrequest.mail.command.domain.MemberMailRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMailJpaRepository extends JpaRepository<MemberMail,Long>, MemberMailRepository {
}
