package com.study.badrequest.member.command.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(Long memberId);

    Member save(Member member);
    List<Member> findAll();

    List<Member> findMembersByEmail(String email);
    List<Member> findMembersByContact(String contact);
    Optional<Member> findMemberByAuthenticationCodeAndCreatedAt(String authenticationCode, LocalDateTime createdAt);
}
