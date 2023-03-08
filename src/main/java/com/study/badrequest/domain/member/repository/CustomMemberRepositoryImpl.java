package com.study.badrequest.domain.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.QMember;
import com.study.badrequest.domain.member.repository.query.MemberSimpleInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.study.badrequest.domain.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 로그인 서비스에서 필요한 정보 쿼리 최적화
     */
    @Override
    public Optional<MemberSimpleInformation> findByUsernameAndAuthority(String email, Authority authority) {

        QMember qMember = member;

        MemberSimpleInformation memberSimpleInformation = jpaQueryFactory
                .select(Projections.fields(MemberSimpleInformation.class,
                        qMember.id.as("id"),
                        qMember.username.as("username"),
                        qMember.authority.as("authority")
                ))
                .from(qMember)
                .where(qMember.email.eq(email).and(qMember.authority.eq(authority)))
                .fetchOne();
        return memberSimpleInformation == null ? Optional.empty() : Optional.of(memberSimpleInformation);
    }

}
