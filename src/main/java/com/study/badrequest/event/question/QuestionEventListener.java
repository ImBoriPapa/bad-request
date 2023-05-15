package com.study.badrequest.event.question;



import com.study.badrequest.service.activity.ActivityServiceImpl;

import com.study.badrequest.service.image.QuestionImageService;
import com.study.badrequest.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Component;



@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionEventListener {
    private final QuestionService questionService;
    private final QuestionImageService questionImageService;
    private final ActivityServiceImpl activityService;

    @EventListener
    public void handleCreateEvent(QuestionEventDto.Create dto) {
        log.info("질문 생성 이벤트");
        questionImageService.changeTemporaryToSaved(dto.getImages(), dto.getQuestion());

        activityService.createActivity(dto.getMember(), dto.getQuestion().getTitle(), dto.getQuestion().getAskedAt());
    }

    @EventListener
    public void handleModifyEvent(QuestionEventDto.Modify dto) {
        log.info("질문 수정 이벤트");

        questionImageService.update(dto.getImages(), dto.getQuestion());

    }

    @EventListener
    public void handlePostViewedEvent(QuestionEventDto.View dto) {
        log.info("질문 조회 이벤트");
        try {
            questionService.incrementViewWithCookie(dto.getRequest(), dto.getResponse(), dto.getQuestionId());
        } catch (Exception e) {
            log.error("조회 수 증가 실패 실패 질문 아이디: {}, 에러 메시지: {}", dto.getQuestionId(), e.getMessage());
        }
    }
}
