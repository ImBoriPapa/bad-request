package com.study.badrequest.repository.member;


import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.dto.member.MemberSearchCondition;

import com.study.badrequest.repository.member.query.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.study.badrequest.domain.member.QMember.member;


@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 회원 계정 정보 조회
     */
    @Override
    public Optional<MemberDetailDto> findMemberDetail(Long memberId) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                MemberDetailDto.class,
                                member.id.as("id"),
                                member.email.as("email"),
                                member.contact.as("contact"),
                                member.memberProfile.nickname.as("nickname"),
                                member.memberProfile.selfIntroduce.as("selfIntroduce"),
                                member.memberProfile.profileImage.imageLocation.as("profileImage"),
                                member.authority.as("authority"),
                                member.registrationType.as("loginType"),
                                member.createdAt.as("createdAt"),
                                member.updatedAt.as("updatedAt")
                        )
                )
                .from(member)
                .where(member.id.eq(memberId))
                .fetch()
                .stream()
                .findFirst();

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
                                member.memberProfile.nickname.as("nickname"),
                                member.memberProfile.profileImage.imageLocation.as("profileImage")
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

    /**
     * 프로필 조회
     */
    @Override
    public Optional<MemberProfileDto> findMemberProfileByMemberId(Long memberId) {

        MemberProfileDto memberProfileDto = jpaQueryFactory
                .select(
                        Projections.fields(MemberProfileDto.class,
                                member.id.as("memberId"),
                                member.memberProfile.nickname.as("nickname"),
                                member.memberProfile.selfIntroduce.as("selfIntroduce"),
                                member.memberProfile.profileImage.imageLocation.as("profileImage")
                        )
                )
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return memberProfileDto == null ? Optional.empty() : Optional.of(memberProfileDto);
    }

    @Override
    public Optional<LoggedInMemberInformation> findLoggedInMemberInformation(Long memberId) {

        return jpaQueryFactory
                .select(Projections.fields(LoggedInMemberInformation.class,
                        member.id.as("id"),
                        member.authority.as("authority"),
                        member.memberProfile.nickname.as("nickname"),
                        member.memberProfile.profileImage.imageLocation.as("profileImage"),
                        member.registrationType.as("loggedInAs")
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetch()
                .stream()
                .findFirst();


    }
}
