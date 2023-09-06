package com.study.badrequest.member.command.domain.repository;

import com.study.badrequest.member.command.domain.model.Member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long memberId);
    Member save(Member member);
    List<Member> findMembersByEmail(String email);
    List<Member> findMembersByContact(String contact);
    Optional<Member> findMemberByAuthenticationCodeAndCreatedAt(String authenticationCode, LocalDateTime createdAt);


}
