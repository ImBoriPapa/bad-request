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
        return null;

    }

    private QBean<MemberInformation> getFields() {
        return null;
    }
}
