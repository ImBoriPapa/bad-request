package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.*;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static com.study.badrequest.commons.status.ExposureStatus.*;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public QuestionResponse.Create createQuestionProcessing(Long memberId, QuestionRequest.Create form) {
        log.info("Create Question Processing memberId: {}, title: {}", memberId, form.getTitle());

        Member member = findMemberById(memberId);

        QuestionMetrics questionMetrics = QuestionMetrics.createQuestionMetrics();
        Question questionEntity = Question.createQuestion(form.getTitle(), form.getContents(), member, questionMetrics);
        Question question = questionRepository.save(questionEntity);

        //이벤트: 1.태그 저장 2.이미지 상태 변경
        applicationEventPublisher.publishEvent(new QuestionEventDto.CreateEvent(member, question, form.getTags(), form.getImageIds()));

        return new QuestionResponse.Create(question.getId(), question.getAskedAt());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }


    @Transactional
    public QuestionResponse.Modify modifyQuestion(Long memberId, Long questionId, QuestionRequest.Modify form) {
        log.info("질문 수정 시작");

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));

        if (!question.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(PERMISSION_DENIED);
        }

        applicationEventPublisher.publishEvent(new QuestionEventDto.ModifyEvent(question, form.getImageIds()));

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }

    @Transactional
    public QuestionResponse.Delete deleteQuestion(Long memberId, Long questionId) {
        log.info("질문 삭제 시작 요청 회원: {}, 질문 아이디: {}", memberId, questionId);
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));

        if (!question.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(PERMISSION_DENIED);
        }

        question.changeExposure(DELETE);

        applicationEventPublisher.publishEvent(new QuestionEventDto.DeleteEvent(question));

        return new QuestionResponse.Delete(questionId, question.getDeletedAt());
    }


}
