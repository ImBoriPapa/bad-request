package com.study.badrequest.question.query.dao;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.login.command.domain.CustomMemberPrincipal;
import com.study.badrequest.question.command.domain.dto.QuestionEventDto;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.question.query.dto.QuestionDetail;
import com.study.badrequest.question.query.dto.QuestionListResult;
import com.study.badrequest.question.query.dto.QuestionSearchCondition;
import com.study.badrequest.question.query.dto.QuestionSearchConditionWithHashTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryServiceImpl implements QuestionQueryService {
    private final QuestionQueryRepository questionQueryRepository;
    private final ApplicationEventPublisher eventPublisher;
    @Override
    @Transactional(readOnly = true)
    public QuestionDetail getQuestionDetail(HttpServletRequest request, HttpServletResponse response, Long questionId, CustomMemberPrincipal information) {
        log.info("질문 조회 서비스");


        QuestionDetail questionDetail = questionQueryRepository.findQuestionDetail(questionId, information.getMemberId(), ExposureStatus.PUBLIC)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_QUESTION));

        eventPublisher.publishEvent(new QuestionEventDto.ViewEvent(request, response, questionId));

        return questionDetail;
    }

    @Override
    public QuestionListResult getQuestionList(QuestionSearchCondition condition) {
        return questionQueryRepository.findQuestionListByCondition(condition);
    }

    @Override
    public QuestionListResult getQuestionListBy(QuestionSearchConditionWithHashTag condition) {
        return questionQueryRepository.findQuestionListByHashTag(condition);
    }
}
