package com.study.badrequest.event.question;


import com.study.badrequest.service.question.QuestionMetricsService;
import com.study.badrequest.service.question.QuestionTagService;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionEventListener {
    private final QuestionTagService questionTagService;

    private final QuestionMetricsService questionMetricsService;
    private final RecordService recordService;



    @EventListener
    public void handleDeleteEvent(QuestionEventDto.DeleteEvent dto) {
        log.info("질문 삭제 이벤트");


    }

    @EventListener
    public void handlePostViewedEvent(QuestionEventDto.ViewEvent dto) {
        log.info("질문 조회 이벤트");
        CompletableFuture.runAsync(() -> questionMetricsService.incrementViewWithCookie(dto.getRequest(), dto.getResponse(), dto.getQuestionId()));
    }
}
