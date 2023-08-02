package com.study.badrequest.event.login;

import com.study.badrequest.record.command.domain.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.event.member.MemberEventDto;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.study.badrequest.config.AsyncConfig.WELCOME_MAIL_ASYNC_EXECUTOR;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Transactional
public class LoginEventListener {

    private final RecordService recordService;

    @Async(WELCOME_MAIL_ASYNC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLoginEvent(MemberEventDto.Login dto) {
        log.info("회원 로그인 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.LOGIN, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }

}
