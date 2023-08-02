package com.study.badrequest.answer.query.dao;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.badrequest.answer.query.dto.AnswerDto;
import com.study.badrequest.answer.query.dto.AnswerResult;
import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.answer.command.domain.AnswerRecommendation;
import com.study.badrequest.recommandation.command.domain.RecommendationKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.study.badrequest.answer.command.domain.QAnswer.answer;
import static com.study.badrequest.answer.command.domain.QAnswerRecommendation.answerRecommendation;
import static com.study.badrequest.member.command.domain.QMember.member;
import static com.study.badrequest.member.command.domain.QMemberProfile.memberProfile;


@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AnswerQueryRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public AnswerResult findAnswerByQuestionId(Long questionId, Long lastOfData, ExposureStatus exposureStatus, Long memberId) {

        BooleanExpression index = lastOfData == null ? null : answer.id.gt(lastOfData);
        BooleanExpression exposure = exposureStatus == null ? answer.exposureStatus.eq(ExposureStatus.PUBLIC) : answer.exposureStatus.eq(exposureStatus);

        int defaultLimitSize = 5;
        int searchLimitSize = defaultLimitSize + 1;
        boolean hasNext = false;
        long last = 0L;

        List<AnswerDto> result = jpaQueryFactory
                .select(
                        Projections.fields(AnswerDto.class,
                                answer.id.as("id"),
                                answer.contents.as("contents"),
                                Expressions.asBoolean(false).as("isAnswerer"),
                                Projections.fields(AnswerDto.Answerer.class,
                                        member.id.as("id"),
                                        memberProfile.nickname.as("nickname"),
                                        memberProfile.profileImage.imageLocation.as("profileImage")
                                ).as("answerer"),
                                Projections.fields(AnswerDto.Metrics.class,
                                        answer.numberOfRecommendation.as("numberOfRecommendation"),
                                        answer.numberOfComment.as("numberOfComment"),
                                        Expressions.asBoolean(false).as("hasRecommendation"),
                                        Expressions.asEnum(RecommendationKind.NOT_EXIST_RECOMMENDATION).as("kind")
                                ).as("metrics"),
                                answer.answeredAt.as("answeredAt"),
                                answer.modifiedAt.as("modifiedAt")
                        )
                )
                .from(answer)
                .join(answer.member, member)
                .join(member.memberProfile, memberProfile)
                .where(
                        answer.question.id.eq(questionId),
                        index,
                        exposure
                )
                .orderBy(answer.id.asc())
                .limit(searchLimitSize)
                .fetch();

        if (result.size() > defaultLimitSize) {
            hasNext = true;
            result.remove(result.size() - 1);
        }

        last = result.stream().mapToLong(AnswerDto::getId).max().orElse(0);

        if (!result.isEmpty() && memberId != null) {
            setRecommendationMetrics(memberId, result);
        }

        return new AnswerResult(result.size(), last, hasNext, result);
    }

    private void setRecommendationMetrics(Long memberId, List<AnswerDto> result) {
        List<Long> answerIds = result.stream().map(AnswerDto::getId).collect(Collectors.toList());

        List<AnswerRecommendation> recommendationList = findAnswerRecommendationInAnswerIds(answerIds);

        if (!recommendationList.isEmpty()) {
            setHasRecommendation(memberId, result, recommendationList);
        }

    }

    private static void setHasRecommendation(Long memberId, List<AnswerDto> result, List<AnswerRecommendation> recommendationList) {
        for (AnswerDto answerDto : result) {
            for (AnswerRecommendation recommendation : recommendationList) {
                if (recommendation.getMember().getId().equals(memberId)) {
                    answerDto.getMetrics().setHasRecommendation(recommendation.getKind());
                }
            }
        }
    }

    private List<AnswerRecommendation> findAnswerRecommendationInAnswerIds(List<Long> answerIds) {
        List<AnswerRecommendation> recommendationList = jpaQueryFactory
                .select(answerRecommendation)
                .from(answerRecommendation)
                .where(
                        answerRecommendation.answer.id.in(answerIds)
                ).fetch();
        return recommendationList;
    }
}
