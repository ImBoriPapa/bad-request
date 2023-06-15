package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Authority;
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
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public QuestionResponse.Create createQuestionProcessing(Long memberId, QuestionRequest.Create form) {
        log.info("Create Question Processing memberId: {}, title: {}", memberId, form.getTitle());

        Member member = findMemberById(memberId);

        QuestionMetrics questionMetrics = QuestionMetrics.createQuestionMetrics();
        Question questionEntity = Question.createQuestion(form.getTitle(), form.getContents(), member, questionMetrics);
        Question question = questionRepository.save(questionEntity);

        //이벤트: 1.태그 저장 2.이미지 상태 변경
        eventPublisher.publishEvent(new QuestionEventDto.CreateEvent(member, question, form.getTags(), form.getImageIds()));

        return new QuestionResponse.Create(question.getId(), question.getAskedAt());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public QuestionResponse.Modify modifyQuestionProcessing(Long memberId, Long questionId, QuestionRequest.Modify form) {
        log.info("Modify Question Processing");

        Member requester = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
        Question question = findQuestionById(questionId);

        checkPermissions(requester, question);

        question.modify(form.getTitle(), form.getContents());

        eventPublisher.publishEvent(new QuestionEventDto.ModifyEvent(question, form.getImageIds()));

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }

    private void checkPermissions(Member requester, Question question) {
        if (requester.getAuthority() != Authority.ADMIN) {
            if (!requester.equals(question.getMember())) {
                throw new CustomRuntimeException(PERMISSION_DENIED);
            }
        }
    }

    @Transactional
    public QuestionResponse.Delete deleteQuestionProcess(Long memberId, Long questionId) {
        log.info("질문 삭제 시작 요청 회원: {}, 질문 아이디: {}", memberId, questionId);
        Question question = findQuestionById(questionId);

        if (!question.getMember().getId().equals(memberId)) {
            throw new CustomRuntimeException(PERMISSION_DENIED);
        }

        question.changeExposure(DELETE);

        eventPublisher.publishEvent(new QuestionEventDto.DeleteEvent(question));

        return new QuestionResponse.Delete(questionId, question.getDeletedAt());
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_QUESTION));
    }


}
