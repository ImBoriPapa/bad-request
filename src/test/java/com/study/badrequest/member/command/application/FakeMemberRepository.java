package com.study.badrequest.member.command.application;

import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.model.MemberId;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeMemberRepository implements MemberRepository {

    private Map<Long, Member> store = new HashMap<>();

    @Override
    public Optional<Member> findById(Long memberId) {
        return Optional.of(store.get(memberId));
    }

    @Override
    public Member save(Member member) {
        return null;
    }

    @Override
    public List<MemberEntity> findAll() {
        return null;
    }

    @Override
    public List<Member> findMembersByEmail(String email) {
        return null;
    }

    @Override
    public List<Member> findMembersByContact(String contact) {
        return null;
    }

    @Override
    public Optional<MemberEntity> findMemberByAuthenticationCodeAndCreatedAt(String authenticationCode, LocalDateTime createdAt) {
        return Optional.empty();
    }
}
