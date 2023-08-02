package com.study.badrequest.event.member;

import com.study.badrequest.record.command.domain.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.record.command.application.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import static com.study.badrequest.config.AsyncConfig.WELCOME_MAIL_ASYNC_EXECUTOR;

@Component
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class MemberUpdateEventListener {
    private final RecordService recordService;
    @Async(WELCOME_MAIL_ASYNC_EXECUTOR)
    @EventListener
    public void handleUpdateEvent(MemberEventDto.Update dto) {
        log.info("회원 업데이트 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.UPDATED, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }
}
