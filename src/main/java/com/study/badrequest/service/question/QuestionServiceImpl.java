package com.study.badrequest.service.question;

import com.study.badrequest.member.command.domain.Authority;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;

import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionMetrics;
import com.study.badrequest.question.command.domain.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;

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

        checkNumberOfTags(form.getTags());

        final List<Long> imageIds = form.getImageIds() == null ? Collections.emptyList() : form.getImageIds();

        Member member = findMemberById(memberId);

        Question question = questionRepository.save(createQuestionEntity(form.getTitle(), form.getContents(), member));

        eventPublisher.publishEvent(new QuestionEventDto.CreateEvent(member.getId(), question.getId(), form.getTags(), imageIds));

        return new QuestionResponse.Create(question.getId(), question.getAskedAt());
    }

    private void checkNumberOfTags(List<String> tags) {
        if (tags == null || tags.isEmpty() || tags.size() > 5) {
            throw CustomRuntimeException.createWithApiResponseStatus(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED);
        }
    }

    private Question createQuestionEntity(String title, String contents, Member member) {
        return Question.createQuestion(title, contents, member, QuestionMetrics.createQuestionMetrics());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public QuestionResponse.Modify modifyQuestionProcessing(Long memberId, Long questionId, QuestionRequest.Modify form) {
        log.info("Modify Question Processing");

        Member requester = findMemberById(memberId);

        Question question = findQuestionById(questionId);

        checkPermissions(requester, question);

        question.modify(form.getTitle(), form.getContents());

        eventPublisher.publishEvent(new QuestionEventDto.ModifyEvent(requester.getId(), question.getId(), form.getImageIds()));

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }

    private void checkPermissions(Member requester, Question question) {
        if (requester.getAuthority() != Authority.ADMIN) {
            if (!requester.equals(question.getMember())) {
                throw CustomRuntimeException.createWithApiResponseStatus(PERMISSION_DENIED);
            }
        }
    }

    @Transactional
    public QuestionResponse.Delete deleteQuestionProcess(Long memberId, Long questionId) {
        log.info("Delete Question Process");
        Member requester = findMemberById(memberId);

        Question question = findQuestionById(questionId);

        checkPermissions(requester, question);

        question.changeExposure(DELETE);

        eventPublisher.publishEvent(new QuestionEventDto.DeleteEvent(question));

        return new QuestionResponse.Delete(questionId, question.getDeletedAt());
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION));
    }


}
