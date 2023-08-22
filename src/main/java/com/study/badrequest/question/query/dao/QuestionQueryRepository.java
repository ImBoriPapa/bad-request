package com.study.badrequest.question.query.dao;


import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.question.query.dto.QuestionSearchCondition;
import com.study.badrequest.question.query.dto.QuestionSearchConditionWithHashTag;
import com.study.badrequest.question.query.dto.QuestionDetail;
import com.study.badrequest.question.query.dto.QuestionListResult;

import java.util.Optional;

public interface QuestionQueryRepository {
    QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition);

    QuestionListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition);

    Optional<QuestionDetail> findQuestionDetail(Long questionId, Long memberId, ExposureStatus exposureStatus);

}
