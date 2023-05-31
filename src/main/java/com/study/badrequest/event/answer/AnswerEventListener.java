package com.study.badrequest.event.answer;

import com.study.badrequest.domain.activity.ActivityAction;
import com.study.badrequest.service.activity.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnswerEventListener {

    private final ActivityService activityService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRegisterEvent(AnswerEventDto.Register dto) {
        log.info("답변 등록 이벤트");
        String simpleTitle = dto.getAnswer().getContents().substring(0, 10);
        activityService.createActivity(dto.getMember(), simpleTitle, ActivityAction.ANSWER,dto.getAnswer().getAnsweredAt());
    }
}
