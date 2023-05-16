package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.question.RecommendationKind;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface QuestionService {

    QuestionResponse.Create createQuestion(Long memberId, QuestionRequest.Create form);

    QuestionResponse.Modify modifyQuestion(Long memberId, Long questionId, QuestionRequest.ModifyForm form);

    QuestionResponse.Delete deleteQuestion(Long memberId, Long questionId);

}
