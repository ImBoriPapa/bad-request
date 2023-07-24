package com.study.badrequest.event.question;

import com.study.badrequest.domain.record.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.service.image.QuestionImageService;
import com.study.badrequest.service.record.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.study.badrequest.domain.record.ActionStatus.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionModifyEventListener {
    private final QuestionImageService questionImageService;

    private final RecordService recordService;
    @EventListener
    public void handleModifyEvent(QuestionEventDto.ModifyEvent dto) {
        log.info("Question Modify Event");

        questionImageService.update(dto.getImages(), dto.getQuestionId());

        recordService.recordMemberInformation(createRecordDto(dto.getMemberId(), dto.getQuestionId()));
    }

    private MemberRecordRequest createRecordDto(Long memberId, Long questionId) {
        final String description = "질문 글을 수정하였습니다. memberId: " + memberId + ", " + "questionId: " + questionId;

        return new MemberRecordRequest(UPDATE_QUESTION, memberId, null, description, LocalDateTime.now());
    }

}
