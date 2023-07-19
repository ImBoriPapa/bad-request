package com.study.badrequest.event.login;

import com.study.badrequest.domain.record.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.config.AsyncConfig.WELCOME_MAIL_ASYNC_EXECUTOR;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Transactional
public class LogoutEventListener {

    private final RecordService recordService;

    @Async(WELCOME_MAIL_ASYNC_EXECUTOR)
    @EventListener
    public void handleLogoutEvent(MemberEventDto.Logout dto) {
        log.info("회원 로그아웃 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.LOGOUT, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }

}
