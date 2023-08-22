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




@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AnswerQueryRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;

    public AnswerResult findAnswerByQuestionId(Long questionId, Long lastOfData, ExposureStatus exposureStatus, Long memberId) {

        return null;
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
        return null;
    }
}
