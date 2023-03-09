package com.study.badrequest.domain.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.repository.query.MemberLoginInformation;
import com.study.badrequest.domain.member.repository.query.MemberSimpleInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.study.badrequest.domain.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<MemberSimpleInformation> findByUsernameAndAuthority(String username, Authority authority) {

        return jpaQueryFactory
                .select(Projections.fields(MemberSimpleInformation.class,
                        member.id.as("id"),
                        member.username.as("username"),
                        member.authority.as("authority")
                ))
                .from(member)
                .where(member.username.eq(username).and(member.authority.eq(authority)))
                .fetch()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<MemberLoginInformation> findLoginInformationByEmail(String email) {
        return jpaQueryFactory
                .select(Projections.fields(MemberLoginInformation.class,
                        member.id.as("id"),
                        member.email.as("email"),
                        member.password.as("password"),
                        member.username.as("username"),
                        member.authority.as("authority")
                ))
                .from(member)
                .where(member.email.eq(email))
                .fetch()
                .stream()
                .findFirst();
    }

}
