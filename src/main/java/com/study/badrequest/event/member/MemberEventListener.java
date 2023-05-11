package com.study.badrequest.event.member;

import com.study.badrequest.dto.record.MemberRecordRequestMapper;
import com.study.badrequest.service.mail.MemberMailService;
import com.study.badrequest.service.mail.NonMemberMailService;
import com.study.badrequest.service.record.RecordServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Transactional
public class MemberEventListener {
    private final MemberMailService mailService;
    private final NonMemberMailService nonMemberMailService;
    private final RecordServiceImpl recordService;

    /**
     * AFTER_COMMIT 설정이후 트랜젝션은 commit 안됨
     * @Async 로 Thread 분리
     */

    @Async//Thread 나누기
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateEvent(MemberEventDto.Create dto) {
        log.info("회원 생성 이벤트 ");
        recordService.recordMemberInformation(MemberRecordRequestMapper.eventDtoToMemberRecordRequest(dto));
    }

    @Async
    @EventListener
    public void handleSendAuthenticationEmail(MemberEventDto.SendAuthenticationMail dto) {
        log.info("인증 메일 발송 이벤트 ");
        nonMemberMailService.sendAuthenticationMail(dto.getAuthenticationMailInformation());
    }

    @Async
    @EventListener
    public void handleUpdateEvent(MemberEventDto.Update dto) {
        log.info("회원 업데이트 이벤트 ");
        recordService.recordMemberInformation(MemberRecordRequestMapper.eventDtoToMemberRecordRequest(dto));
    }

    @Async
    @EventListener
    public void handleDeleteEvent(MemberEventDto.Delete dto) {
        log.info("회원 삭제 이벤트 ");
        recordService.recordMemberInformation(MemberRecordRequestMapper.eventDtoToMemberRecordRequest(dto));
    }

    @Async
    @EventListener
    public void handleIssueTemporaryPassword(MemberEventDto.IssueTemporaryPassword dto) {
        log.info("회원 임시 비밀번호 이벤트 ");
        mailService.sendTemporaryPassword(dto.getMember(),dto.getTemporaryPassword());
        recordService.recordMemberInformation(MemberRecordRequestMapper.eventDtoToMemberRecordRequest(dto));
    }

    @Async//Thread 나누기
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLoginEvent(MemberEventDto.Login dto) {
        log.info("회원 로그인 이벤트 ");
        recordService.recordMemberInformation(MemberRecordRequestMapper.eventDtoToMemberRecordRequest(dto));
    }

    @Async
    @EventListener
    public void handleLogoutEvent(MemberEventDto.Logout dto) {
        log.info("회원 로그아웃 이벤트 ");
        recordService.recordMemberInformation(MemberRecordRequestMapper.eventDtoToMemberRecordRequest(dto));
    }
}
