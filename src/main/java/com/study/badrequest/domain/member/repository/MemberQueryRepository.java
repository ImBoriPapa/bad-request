package com.study.badrequest.domain.member.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.member.dto.MemberDto;
import com.study.badrequest.domain.member.dto.MemberInfoDto;
import com.study.badrequest.domain.member.entity.Authority;

import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.QMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<MemberInfoDto> findIdAndAuthorityByUsername(String username) {

        QMember qMember = QMember.member;

        MemberInfoDto infoDto = jpaQueryFactory
                .select(
                        Projections.fields(MemberInfoDto.class,
                                qMember.member.id.as("id"),
                                qMember.authority.as("authority"))
                )
                .from(qMember)
                .where(qMember.username.eq(username))
                .fetchOne();

        return infoDto == null ? Optional.empty() : Optional.of(infoDto);
    }

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

        return memberDto == null ? Optional.empty() : Optional.of(memberDto);
    }
}
