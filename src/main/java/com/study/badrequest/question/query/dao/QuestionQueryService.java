package com.study.badrequest.question.query.dao;

import com.study.badrequest.login.command.domain.CurrentMember;
import com.study.badrequest.question.query.dto.QuestionDetail;
import com.study.badrequest.question.query.dto.QuestionListResult;
import com.study.badrequest.question.query.dto.QuestionSearchCondition;
import com.study.badrequest.question.query.dto.QuestionSearchConditionWithHashTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface QuestionQueryService {

    QuestionDetail getQuestionDetail(HttpServletRequest request, HttpServletResponse response, Long questionId, CurrentMember.Information information);

    QuestionListResult getQuestionList(QuestionSearchCondition condition);

    QuestionListResult getQuestionListBy(QuestionSearchConditionWithHashTag condition);

}
