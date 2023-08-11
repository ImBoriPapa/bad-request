package com.study.badrequest.mail.command.infra.persistence;

import com.study.badrequest.mail.command.domain.NonMemberMail;
import com.study.badrequest.mail.command.domain.NonMemberMailRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NonMemberMailJpaRepository extends JpaRepository<NonMemberMail,Long>, NonMemberMailRepository {
}
