package com.study.badrequest.Member.domain.repository;

import com.study.badrequest.Member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
