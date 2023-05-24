package com.study.badrequest.service.question;

import com.study.badrequest.domain.login.CurrentMember;
import com.study.badrequest.repository.question.query.QuestionDetail;
import com.study.badrequest.repository.question.query.QuestionListResult;
import com.study.badrequest.repository.question.query.QuestionSearchCondition;
import com.study.badrequest.repository.question.query.QuestionSearchConditionWithHashTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface QuestionQueryService {

    QuestionDetail getQuestionDetail(HttpServletRequest request, HttpServletResponse response, Long questionId, CurrentMember.Information information);

    QuestionListResult getQuestionList(QuestionSearchCondition condition);

    QuestionListResult getQuestionListBy(QuestionSearchConditionWithHashTag condition);

}
