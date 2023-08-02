package com.study.badrequest.event.member;

import com.study.badrequest.record.command.domain.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.service.blog.BlogService;
import com.study.badrequest.service.mail.MemberMailService;
import com.study.badrequest.service.member.MemberProfileService;
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
public class MemberCreateEventListener {
    private final MemberProfileService memberProfileService;
    private final RecordService recordService;
    private final BlogService blogService;
    private final MemberMailService memberMailService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCreateEvent(MemberEventDto.Create dto) {
        log.info("Create Member Event");

        memberProfileService.createMemberProfileProcessing(dto.getMemberId(), dto.getNickname());

        MemberRecordRequest memberRecordRequest = new MemberRecordRequest(ActionStatus.CREATED, dto.getMemberId(), dto.getIpAddress(), dto.getDescription(), dto.getRecordTime());

        recordService.recordMemberInformation(memberRecordRequest);

        blogService.createBlog(dto.getMemberId());
    }

    @Async(WELCOME_MAIL_ASYNC_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void async(MemberEventDto.Create dto) {
        memberMailService.sendWelcome(dto.getMemberId());
    }
}
