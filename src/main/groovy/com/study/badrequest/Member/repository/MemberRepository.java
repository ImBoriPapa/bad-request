package com.study.badrequest.Member.repository;

import com.study.badrequest.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
