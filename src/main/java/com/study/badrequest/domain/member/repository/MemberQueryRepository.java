package com.study.badrequest.domain.member.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.member.dto.MemberUsernameDetailDto;
import com.study.badrequest.domain.member.dto.MemberAuthDto;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.QMember;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.study.badrequest.domain.member.entity.QMember.*;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Member> findMember(Long memberId) {
        Member findMember = jpaQueryFactory.select(member)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
        return findMember == null ? Optional.empty() : Optional.of(findMember);
    }

    /**
     * username 으로 회원 권한 정보 조회
     */
    public Optional<MemberAuthDto> findIdAndAuthorityByUsername(String username, Authority authority) {

        MemberAuthDto infoDto = jpaQueryFactory
                .select(
                        Projections.fields(MemberAuthDto.class,
                                member.id.as("id"),
                                member.authority.as("authority"))
                )
                .from(member)
                .where(member.username.eq(username),
                        authorityEq(authority))
                .fetchOne();

        return infoDto == null ? Optional.empty() : Optional.of(infoDto);
    }

    private BooleanExpression authorityEq(Authority authority) {
        return authority == null ? null : member.authority.eq(authority);
    }


    public Optional<MemberDtoForLogin> findByEmail(String email) {

        QMember qMember = member;

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
        return memberDtoForLogin == null ? Optional.empty() : Optional.of(memberDtoForLogin);
    }

    public Optional<MemberUsernameDetailDto> findByUsername(String username) {
        QMember qMember = member;

        MemberUsernameDetailDto memberUsernameDetailDto = jpaQueryFactory
                .select(Projections.fields(MemberUsernameDetailDto.class,
                        qMember.username.as("username"),
                        qMember.password.as("password"),
                        qMember.authority.as("authority")
                )).from(qMember)
                .where(qMember.username.eq(username))
                .fetchOne();

        return memberUsernameDetailDto == null ? Optional.empty() : Optional.of(memberUsernameDetailDto);
    }
}
