package com.study.badrequest.question.command.infra.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.question.command.domain.MemberInformation;
import com.study.badrequest.question.command.domain.MemberInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static com.study.badrequest.member.command.domain.QMember.*;

@Repository
@Transactional
@RequiredArgsConstructor
public class MemberInformationRepositoryImpl implements MemberInformationRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 회원 정보 단건 조회
     *
     * @param id 회원 아이디 Long
     * @return Optional<MemberInformation>
     */
    @Override
    public Optional<MemberInformation> findById(Long id) {
        return jpaQueryFactory
                .select(getFields())
                .from(member)
                .where(member.id.eq(id))
                .fetch()
                .stream()
                .findFirst();

    }

    private QBean<MemberInformation> getFields() {
        return Projections.fields(MemberInformation.class,
                member.id.as("memberId"),
                member.memberProfile.nickname.as("nickname"),
                member.memberProfile.profileImage.imageLocation.as("profileImage"),
                member.memberProfile.activityScore.as("activityScore"),
                member.authority.as("authority"));
    }
}
