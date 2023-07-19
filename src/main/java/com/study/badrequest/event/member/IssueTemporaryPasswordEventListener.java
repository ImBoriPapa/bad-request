package com.study.badrequest.event.member;

import com.study.badrequest.domain.record.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.service.mail.MemberMailService;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.study.badrequest.config.AsyncConfig.WELCOME_MAIL_ASYNC_EXECUTOR;
@Component
@Slf4j
@RequiredArgsConstructor
public class IssueTemporaryPasswordEventListener {

    private final MemberMailService mailService;
    private final RecordService recordService;

    @Async(WELCOME_MAIL_ASYNC_EXECUTOR)
    @EventListener
    public void handleIssueTemporaryPassword(MemberEventDto.IssueTemporaryPassword dto) {
        log.info("회원 임시 비밀번호 이벤트 ");

        mailService.sendTemporaryPassword(dto.getMemberId(), dto.getTemporaryPassword());

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.ISSUE_TEMPORARY_PASSWORD, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());
        recordService.recordMemberInformation(memberRecordRequest);
    }

}
