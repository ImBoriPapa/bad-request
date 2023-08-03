package com.study.badrequest.question.command.application;

import com.study.badrequest.question.query.interfaces.QuestionResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface QuestionMetricsService {
    QuestionResponse.Modify createRecommendation(Long memberId, Long questionId);
    QuestionResponse.Modify deleteRecommendation(Long memberId, Long questionId);
    void incrementViewWithCookie(HttpServletRequest request, HttpServletResponse response, Long questionId);
}
