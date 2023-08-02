package com.study.badrequest.question.query;


import com.study.badrequest.commons.status.ExposureStatus;

import java.util.Optional;

public interface QuestionQueryRepository {
    QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition);

    QuestionListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition);

    Optional<QuestionDetail> findQuestionDetail(Long questionId, Long memberId, ExposureStatus exposureStatus);

}
