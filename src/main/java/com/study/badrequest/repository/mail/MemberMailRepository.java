package com.study.badrequest.repository.mail;


import com.study.badrequest.mail.command.domain.MemberMail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMailRepository extends JpaRepository<MemberMail,Long> {
}
