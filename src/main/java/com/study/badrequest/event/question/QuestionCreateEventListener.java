package com.study.badrequest.event.question;

import com.study.badrequest.record.command.domain.ActionStatus;
import com.study.badrequest.dto.record.MemberRecordRequest;
import com.study.badrequest.image.command.application.QuestionImageService;
import com.study.badrequest.question.command.application.QuestionTagService;
import com.study.badrequest.record.command.application.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionCreateEventListener {
    private final QuestionTagService questionTagService;
    private final QuestionImageService questionImageService;
    private final RecordService recordService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCreateQuestionEvent(QuestionEventDto.CreateEvent dto) {
        log.info("Question Create Event");

        final Long memberId = dto.getMemberId();
        final Long questionId = dto.getQuestionId();

        questionTagService.createQuestionTagProcessing(questionId, dto.getTags());
        
        if (!dto.getImages().isEmpty()) {
            questionImageService.changeTemporaryToSaved(questionId, dto.getImages());
        }

        recordService.recordMemberInformation(createRecordDto(memberId, questionId));
    }

    private MemberRecordRequest createRecordDto(Long memberId, Long questionId) {
        final String description = "질문 글을 작성하였습니다. memberId: " + memberId + ", " + "questionId: " + questionId;

        return new MemberRecordRequest(ActionStatus.CREATE_QUESTION, memberId, null, description, LocalDateTime.now());
    }
}
