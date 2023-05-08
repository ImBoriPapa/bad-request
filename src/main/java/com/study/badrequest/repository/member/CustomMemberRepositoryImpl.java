package com.study.badrequest.repository.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.repository.member.query.MemberSimpleInformation;
import com.study.badrequest.repository.member.query.MemberUserDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.study.badrequest.domain.member.QMember.member;


@Repository
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    /**
     * UsernameDetailService -> User 객체 생성용 쿼리 최적화
     *
     * @return String username;
     * String password;
     * Authority authority;
     */
    @Override
    public Optional<MemberUserDetailDto> findUserDetailByUsername(String username) {

        return jpaQueryFactory
                .select(Projections.fields(MemberUserDetailDto.class,
                        member.username.as("username"),
                        member.password.as("password"),
                        member.authority.as("authority")
                ))
                .from(member)
                .where(member.username.eq(username))
                .fetch()
                .stream()
                .findFirst();

    }

}
