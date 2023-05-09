package com.study.badrequest.repository.question.query;



import java.util.Optional;

public interface QuestionQueryRepository {
    QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition);

    QuestionListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition);

    Optional<QuestionDetail> findQuestionDetail(Long questionId,Long memberId);

}
