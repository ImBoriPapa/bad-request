package com.study.badrequest.member.command.domain.repository;


import com.study.badrequest.member.command.domain.model.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile,Long> {
}
