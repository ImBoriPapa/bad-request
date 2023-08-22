package com.study.badrequest.member.command.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
    List<MemberEntity> findMemberEntityByEmail(String email);

    List<MemberEntity> findAllByContact(String contact);
}
