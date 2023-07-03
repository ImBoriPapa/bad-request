package com.study.badrequest.repository.member;


import com.study.badrequest.domain.memberProfile.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileRepository extends JpaRepository<MemberProfile,Long> {
}
