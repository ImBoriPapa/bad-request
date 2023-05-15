package com.study.badrequest.repository.question.query;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface QuestionQueryRepository {
    QuestionListResult findQuestionListByCondition(QuestionSearchCondition condition);

    QuestionListResult findQuestionListByHashTag(QuestionSearchConditionWithHashTag condition);

    Optional<QuestionDetail> findQuestionDetail(HttpServletRequest request, HttpServletResponse response,Long questionId, Long memberId);

}
