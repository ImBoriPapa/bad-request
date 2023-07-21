package com.study.badrequest.event.question;


import com.study.badrequest.domain.activity.ActivityAction;
import com.study.badrequest.domain.record.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.service.activity.ActivityService;


import com.study.badrequest.service.image.QuestionImageService;
import com.study.badrequest.service.question.QuestionMetricsService;
import com.study.badrequest.service.question.QuestionTagService;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.study.badrequest.config.AsyncConfig.QUESTION_IMAGE_ASYNC_EXECUTOR;


@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionEventListener {
    private final QuestionTagService questionTagService;
    private final QuestionImageService questionImageService;
    private final QuestionMetricsService questionMetricsService;
    private final RecordService recordService;

    @EventListener
    public void handleModifyEvent(QuestionEventDto.ModifyEvent dto) {
        log.info("질문 수정 이벤트");

        questionImageService.update(dto.getImages(), dto.getQuestion());

    }

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
