package com.study.badrequest.repository.answer.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.domain.member.QMember.*;
import static com.study.badrequest.domain.member.QMemberProfile.*;
import static com.study.badrequest.domain.question.QAnswer.*;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AnswerQueryRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;

    public List<AnswerDto> findAnswerByQuestionId(Long questionId) {

        List<AnswerDto> answerDtos = jpaQueryFactory
                .select(
                        Projections.fields(AnswerDto.class,
                                answer.id.as("id"),
                                answer.contents.as("contents"),
                                answer.numberOfRecommendation.as("numberOfRecommendation"),
                                Projections.fields(AnswerDto.AnswererDto.class,
                                        member.id.as("id"),
                                        memberProfile.nickname.as("nickname"),
                                        memberProfile.profileImage.imageLocation.as("profileImage")
                                ).as("answerer"),
                                answer.answeredAt.as("answeredAt"),
                                answer.modifiedAt.as("modifiedAt")
                        )
                )
                .from(answer)
                .join(answer.member, member)
                .join(member.memberProfile, memberProfile)
                .where(answer.question.id.eq(questionId))
                .fetch();

        return answerDtos;
    }
}
