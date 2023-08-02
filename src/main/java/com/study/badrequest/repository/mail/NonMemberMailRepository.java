package com.study.badrequest.repository.mail;


import com.study.badrequest.mail.command.domain.NonMemberMail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NonMemberMailRepository extends JpaRepository<NonMemberMail,Long> {
}
