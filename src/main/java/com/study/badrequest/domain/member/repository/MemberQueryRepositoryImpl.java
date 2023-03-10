package com.study.badrequest.domain.member.repository;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.domain.member.dto.MemberSearchCondition;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.QMember;
import com.study.badrequest.domain.member.repository.query.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.study.badrequest.domain.member.entity.QMember.*;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 회원 정보 상세 조회
     */
    @Override
    public Optional<MemberDetailDto> findMemberDetail(Long memberId) {
        MemberDetailDto memberDetailDto = jpaQueryFactory
                .select(
                        Projections.fields(MemberDetailDto.class,
                                member.id.as("id"),
                                member.email.as("email"),
                                member.nickname.as("nickname"),
                                member.contact.as("contact"),
                                member.profileImage.fullPath.as("profileImagePath"),
                                member.authority.as("authority"),
                                member.createdAt.as("createdAt"),
                                member.updatedAt.as("updatedAt")
                        )
                )
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return memberDetailDto == null ? Optional.empty() : Optional.of(memberDetailDto);
    }

    // TODO: 2023/02/12 "검색 조건 추가"
    /**
     * 회원 목록 조회
     */
    @Override
    public MemberListDto findMemberList(MemberSearchCondition condition) {

        Long offSet = condition.getOffset();
        Integer size = condition.getSize();
        Order order = condition.getOrder();

        offSet = Objects.requireNonNullElse(offSet, 0L);
        size = Objects.requireNonNullElse(size, 10);
        order = Objects.requireNonNullElse(order, Order.DESC);

        List<MemberListResult> memberListResults = jpaQueryFactory
                .select(
                        Projections.fields(
                                MemberListResult.class,
                                member.id.as("id"),
                                member.email.as("email"),
                                member.nickname.as("nickname"),
                                member.profileImage.fullPath.as("profileImagePath")
                        )
                )
                .from(member)
                .offset(offSet)
                .limit(size)
                .orderBy(new OrderSpecifier<>(order, member.id))
                .fetch();


        long totalMembers = getCount().orElse(0L);
        long totalPages = (long) Math.ceil((double) totalMembers / size);
        long currentPageNumber = (offSet / size) + 1;
        boolean first = (offSet == 0);
        boolean last = ((offSet + size) >= totalMembers);

        return MemberListDto.builder()
                .offSet(offSet)
                .size(size)
                .order(order)
                .first(first)
                .last(last)
                .currentPageNumber(currentPageNumber)
                .totalElements(memberListResults.size())
                .totalMembers(totalMembers)
                .totalPages(totalPages)
                .memberListResults(memberListResults)
                .build();
    }

    private Optional<Long> getCount() {
        Long count = jpaQueryFactory
                .select(member.count())
                .from(member)
                .fetchOne();
        return count == null ? Optional.empty() : Optional.of(count);
    }

    @Override
    public Optional<MemberProfileDto> findMemberProfileByMemberId(Long memberId) {

        MemberProfileDto memberProfileDto = jpaQueryFactory
                .select(
                        Projections.fields(MemberProfileDto.class,
                                member.id.as("memberId"),
                                member.nickname.as("nickname"),
                                member.aboutMe.as("aboutMe"),
                                member.profileImage.fullPath.as("profileImagePath")
                        )
                )
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
        return memberProfileDto == null ? Optional.empty() : Optional.of(memberProfileDto);
    }

    /**
     * username 으로 회원 권한 정보 조회
     */
    @Override
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


}
