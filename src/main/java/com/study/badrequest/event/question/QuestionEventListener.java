package com.study.badrequest.event.question;


import com.study.badrequest.service.activity.ActivityService;


import com.study.badrequest.service.image.QuestionImageService;
import com.study.badrequest.service.question.QuestionMetricsService;
import com.study.badrequest.service.question.QuestionTagService;
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
    private final QuestionImageService questionImageService;
    private final QuestionMetricsService questionMetricsService;
    private final ActivityService activityService;
    @EventListener
    public void handleCreateEvent(QuestionEventDto.CreateEvent dto) {
        log.info("질문 생성 이벤트 수신");

        questionTagService.createQuestionTag(dto.getTags(), dto.getQuestion());

        questionImageService.changeTemporaryToSaved(dto.getImages(), dto.getQuestion());

        activityService.createQuestionActivity(dto.getMember(), dto.getQuestion().getTitle(), dto.getQuestion().getAskedAt());
    }

    @EventListener
    public void handleModifyEvent(QuestionEventDto.ModifyEvent dto) {
        log.info("질문 수정 이벤트");

        questionImageService.update(dto.getImages(), dto.getQuestion());

    }

    @EventListener
    public void handleDeleteEvent(QuestionEventDto.DeleteEvent dto){
        log.info("질문 삭제 이벤트");


    }

    @EventListener
    public void handlePostViewedEvent(QuestionEventDto.ViewEvent dto) {
        log.info("질문 조회 이벤트");
        CompletableFuture.runAsync(() -> questionMetricsService.incrementViewWithCookie(dto.getRequest(), dto.getResponse(), dto.getQuestionId()));
    }
}
