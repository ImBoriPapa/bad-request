package com.study.badrequest.repository.question.query;



import java.util.Optional;

public interface QuestionQueryRepository {
    QuestionDtoListResult findQuestionListByCondition(QuestionSearchCondition condition);

    QuestionDtoListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition);

    Optional<QuestionDetail> findQuestionDetail(Long questionId,Long memberId);

}
