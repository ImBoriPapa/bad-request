package com.study.badrequest.event.question;


import com.study.badrequest.service.activity.ActivityServiceImpl;
import com.study.badrequest.service.hashTag.QuestionHashTagService;
import com.study.badrequest.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class QuestionEventListener {
    private final QuestionHashTagService questionHashTagService;
    private final QuestionService questionService;
    private final ActivityServiceImpl activityService;

    @EventListener
    public void handleCreateEvent(QuestionEventDto.Create dto) {
        log.info("질문 생성 이벤트");

        questionHashTagService.createHashTag(dto.getTags(), dto.getQuestion());

        activityService.createActivity(dto.getMember(), dto.getQuestion().getTitle(), dto.getQuestion().getId(), dto.getQuestion().getAskedAt());
    }

    @EventListener
    public void handlePostViewedEvent(QuestionEventDto.View dto) {
        log.info("질문 조회 이벤트");
        try {
            questionService.incrementViewCount(dto.getQuestionId(), dto.getExposureStatus());
        } catch (Exception e) {
            log.error("조회 수 증가 실패 실패 질문 아이디: {}, 에러 메시지: {}",dto.getQuestionId(),e.getMessage());
        }
    }
}
