package com.study.badrequest.answer.command.application;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.answer.command.domain.Answer;
import com.study.badrequest.answer.command.domain.AnswerComment;
import com.study.badrequest.member.command.infra.persistence.MemberEntity;
import com.study.badrequest.answer.command.interfaces.AnswerCommentRequest;
import com.study.badrequest.answer.command.interfaces.AnswerCommentResponse;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.answer.command.domain.AnswerRepository;
import com.study.badrequest.answer.command.domain.AnswerCommentRepository;
import com.study.badrequest.member.command.domain.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerCommentServiceImpl implements AnswerCommentService {

    private final AnswerCommentRepository answerCommentRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public AnswerCommentResponse.Add addComment(Long memberId, Long answerId, AnswerCommentRequest.Add form) {
        log.info("답변 댓글 추가");

        MemberEntity member = null;
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_ANSWER));

        AnswerComment answerComment = AnswerComment.builder()
                .contents(form.getContents())
                .exposureStatus(ExposureStatus.PUBLIC)
                .writer(member)
                .answer(answer)
                .addedAt(LocalDateTime.now())
                .build();

        AnswerComment saved = answerCommentRepository.save(answerComment);

        return new AnswerCommentResponse.Add(saved.getId(), saved.getAddedAt());
    }

    @Transactional
    public AnswerCommentResponse.Delete deleteComment(Long memberId, Long answerId) {
        log.info("답변 댓글 삭제");
        MemberEntity member = null;
        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(ApiResponseStatus.NOT_FOUND_ANSWER));
        answer.statusToDelete();
        return new AnswerCommentResponse.Delete(answer.getDeletedAt());
    }
}
