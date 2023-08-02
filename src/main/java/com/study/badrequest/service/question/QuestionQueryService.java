package com.study.badrequest.service.question;

import com.study.badrequest.member.command.domain.CurrentMember;
import com.study.badrequest.question.query.QuestionDetail;
import com.study.badrequest.question.query.QuestionListResult;
import com.study.badrequest.question.query.QuestionSearchCondition;
import com.study.badrequest.question.query.QuestionSearchConditionWithHashTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface QuestionQueryService {

    QuestionDetail getQuestionDetail(HttpServletRequest request, HttpServletResponse response, Long questionId, CurrentMember.Information information);

    QuestionListResult getQuestionList(QuestionSearchCondition condition);

    QuestionListResult getQuestionListBy(QuestionSearchConditionWithHashTag condition);

}
