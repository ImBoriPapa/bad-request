package com.study.badrequest.member.command.infra.persistence;

import com.study.badrequest.member.command.domain.model.Member;
import com.study.badrequest.member.command.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long memberId) {
        return memberJpaRepository.findById(memberId).map(MemberEntity::toModel);
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(MemberEntity.fromModel(member)).toModel();
    }

    @Override
    public List<MemberEntity> findAll() {
        return null;
    }

    @Override
    public List<Member> findMembersByEmail(String email) {
        return memberJpaRepository.findMemberEntityByEmail(email)
                .stream()
                .map(MemberEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Member> findMembersByContact(String contact) {
        return memberJpaRepository.findAllByContact(contact).stream()
                .map(MemberEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<MemberEntity> findMemberByAuthenticationCodeAndCreatedAt(String authenticationCode, LocalDateTime createdAt) {
        return Optional.empty();
    }
}
