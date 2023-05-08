package com.study.badrequest.repository.mail;


import com.study.badrequest.domain.mail.MemberMail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMailRepository extends JpaRepository<MemberMail,Long> {
}
