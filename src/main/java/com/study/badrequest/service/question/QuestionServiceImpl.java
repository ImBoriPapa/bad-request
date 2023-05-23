package com.study.badrequest.service.question;

import com.study.badrequest.commons.status.ExposureStatus;
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
import static com.study.badrequest.utils.verification.WordValidateUtils.findBannedWord;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public QuestionResponse.Create createQuestion(Long memberId, QuestionRequest.Create form) {
        log.info("질문 생성 시작 요청- memberId: {}, title: {}", memberId, form.getTitle());

        validateForm(form);

        Member member = findMemberById(memberId);

        Question question = questionRepository.save(createQuestionEntity(form, member));

        question.addQuestionMetrics(QuestionMetrics.createQuestionMetrics());

        //이벤트: 1.태그 저장 2.이미지 상태 변경 2. 회원 활동 점수 변경
        applicationEventPublisher.publishEvent(new QuestionEventDto.CreateEvent(member, question, form.getTags(), form.getImageIds()));

        return new QuestionResponse.Create(question.getId(), question.getAskedAt());
    }

    private Question createQuestionEntity(QuestionRequest.Create form, Member member) {
        return Question.createQuestion()
                .title(form.getTitle())
                .contents(form.getContents())
                .member(member)
                .build();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(NOTFOUND_MEMBER));
    }

    private void validateForm(QuestionRequest.Create form) {

        findBannedWord(form.getTitle());

        findBannedWord(form.getContents());

        if (form.getTags().size() < 1 || form.getTags().size() > 5) {
            throw new CustomRuntimeException(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED);
        }
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

        return new QuestionResponse.Delete(questionId, question.getDeletedRequestAt());
    }


}
