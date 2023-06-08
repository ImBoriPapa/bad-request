package com.study.badrequest.event.member;

import com.study.badrequest.domain.record.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;

import com.study.badrequest.service.mail.MemberMailService;
import com.study.badrequest.service.mail.NonMemberMailService;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.study.badrequest.config.AsyncConfig.RECORD_ASYNC_EXECUTOR;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Transactional
public class MemberEventListener {
    private final MemberMailService mailService;
    private final NonMemberMailService nonMemberMailService;
    private final RecordService recordService;

    @Async(RECORD_ASYNC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateEvent(MemberEventDto.Create dto) {
        log.info("Create Member Event");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.CREATED, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }

    @Async(RECORD_ASYNC_EXECUTOR)
    @EventListener
    public void handleSendAuthenticationEmail(MemberEventDto.SendAuthenticationMail dto) {
        log.info("인증 메일 발송 이벤트 ");
        nonMemberMailService.sendAuthenticationMail(dto.getAuthenticationCode());
    }

    @Async(RECORD_ASYNC_EXECUTOR)
    @EventListener
    public void handleUpdateEvent(MemberEventDto.Update dto) {
        log.info("회원 업데이트 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.UPDATED, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());


        recordService.recordMemberInformation(memberRecordRequest);
    }

    @Async(RECORD_ASYNC_EXECUTOR)
    @EventListener
    public void handleDeleteEvent(MemberEventDto.Delete dto) {
        log.info("회원 삭제 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.DELETED, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }

    @Async(RECORD_ASYNC_EXECUTOR)
    @EventListener
    public void handleIssueTemporaryPassword(MemberEventDto.IssueTemporaryPassword dto) {
        log.info("회원 임시 비밀번호 이벤트 ");

        mailService.sendTemporaryPassword(dto.getMemberId(), dto.getTemporaryPassword());

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.ISSUE_TEMPORARY_PASSWORD, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());
        recordService.recordMemberInformation(memberRecordRequest);
    }

    @Async(RECORD_ASYNC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLoginEvent(MemberEventDto.Login dto) {
        log.info("회원 로그인 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.LOGIN, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }

    @Async(RECORD_ASYNC_EXECUTOR)
    @EventListener
    public void handleLogoutEvent(MemberEventDto.Logout dto) {
        log.info("회원 로그아웃 이벤트 ");

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.LOGOUT, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);
    }
}
