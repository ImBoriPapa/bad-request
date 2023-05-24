package com.study.badrequest.service.question;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.question.query.*;
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
    public QuestionDetail getQuestionDetail(HttpServletRequest request, HttpServletResponse response, Long questionId, CurrentLoggedInMember.Information information) {
        log.info("질문 조회 서비스");
        Long memberId = information == null ? null : information.getId();

        QuestionDetail questionDetail = questionQueryRepository.findQuestionDetail(questionId, memberId, ExposureStatus.PUBLIC)
                .orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOT_FOUND_QUESTION));

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