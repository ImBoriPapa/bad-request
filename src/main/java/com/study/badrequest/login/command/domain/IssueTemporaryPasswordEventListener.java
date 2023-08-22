package com.study.badrequest.login.command.domain;

import com.study.badrequest.member.command.domain.model.MemberEventDto;
import com.study.badrequest.record.command.domain.ActionStatus;
import com.study.badrequest.record.command.application.MemberRecordRequest;
import com.study.badrequest.mail.command.application.MemberMailService;
import com.study.badrequest.record.command.application.RecordService;
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
