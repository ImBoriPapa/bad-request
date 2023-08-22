package com.study.badrequest.member.query.dao;



import com.querydsl.jpa.impl.JPAQueryFactory;

import com.study.badrequest.member.query.interfaces.MemberSearchCondition;

import com.study.badrequest.member.query.dto.LoggedInMemberInformation;
import com.study.badrequest.member.query.dto.MemberDetailDto;
import com.study.badrequest.member.query.dto.MemberListDto;

import com.study.badrequest.member.query.dto.MemberProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;




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
        return null;

    }

    // TODO: 2023/02/12 "검색 조건 추가"

    /**
     * 회원 목록 조회
     */
    @Override
    public MemberListDto findMemberList(MemberSearchCondition condition) {

        return null;
    }


    /**
     * 프로필 조회
     */
    @Override
    public Optional<MemberProfileDto> findMemberProfileByMemberId(Long memberId) {

        return null;
    }

    @Override
    public Optional<LoggedInMemberInformation> findLoggedInMemberInformation(Long memberId) {
        return null;


    }
}
