package com.study.badrequest.domain.Member.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.Member.entity.Authority;

import com.study.badrequest.domain.Member.entity.QMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReadOnlyRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<MemberDtoForLogin> findByEmail(String email) {

        QMember qMember = QMember.member;

        MemberDtoForLogin memberDtoForLogin = jpaQueryFactory
                .select(Projections.fields(MemberDtoForLogin.class,
                        qMember.id.as("id"),
                        qMember.email.as("email"),
                        qMember.username.as("username"),
                        qMember.password.as("password")
                ))
                .from(qMember)
                .where(qMember.email.eq(email))
                .fetchOne();
        return memberDtoForLogin == null ? Optional.empty() : Optional.ofNullable(memberDtoForLogin);
    }

    public Optional<MemberDto> findByUsername(String username) {
        QMember qMember = QMember.member;

        MemberDto memberDto = jpaQueryFactory
                .select(Projections.fields(MemberDto.class,
                        qMember.username.as("username"),
                        qMember.password.as("password"),
                        qMember.authority.as("authority")
                )).from(qMember)
                .where(qMember.username.eq(username))
                .fetchOne();

        return memberDto == null ? Optional.empty() : Optional.ofNullable(memberDto);
    }

    @Getter
    @NoArgsConstructor
    public static class MemberDto {
        private String username;
        private String password;
        private Authority authority;
    }
}
