package com.study.badrequest.question.command.application;

import com.study.badrequest.member.command.domain.Authority;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.question.command.domain.*;
import com.study.badrequest.question.query.interfaces.QuestionRequest;
import com.study.badrequest.question.query.interfaces.QuestionResponse;
import com.study.badrequest.common.exception.CustomRuntimeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;

import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.common.status.ExposureStatus.*;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {
    private final MemberInformationRepository memberInformationRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public QuestionResponse.Create createQuestionProcessing(Long memberId, QuestionRequest.Create form) {
        log.info("Create Question Processing memberId: {}, title: {}", memberId, form.getTitle());

        checkNumberOfTags(form.getTags());

        final List<Long> imageIds = form.getImageIds() == null ? Collections.emptyList() : form.getImageIds();

        MemberInformation member = findMemberById(memberId);

        Question question = questionRepository.save(createQuestionEntity(form.getTitle(), form.getContents(), member));

        eventPublisher.publishEvent(new QuestionEventDto.CreateEvent(member.getMemberId(), question.getId(), form.getTags(), imageIds));

        return new QuestionResponse.Create(question.getId(), question.getAskedAt());
    }

    private void checkNumberOfTags(List<String> tags) {
        if (tags == null || tags.isEmpty() || tags.size() > 5) {
            throw CustomRuntimeException.createWithApiResponseStatus(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED);
        }
    }

    private Question createQuestionEntity(String title, String contents, MemberInformation memberInformation) {
        WriterType writerType = WriterType.MEMBER;

        Authority authority = memberInformation.getAuthority();

        if (authority == Authority.ADMIN) {
            writerType = WriterType.ADMIN;
        }

        Writer writer = Writer.createWriter(memberInformation.getMemberId(), memberInformation.getNickname(), memberInformation.getProfileImage(), memberInformation.getActivityScore(), writerType);
        return Question.createQuestion(title, contents, writer,Collections.emptyList() ,QuestionMetrics.createQuestionMetrics());
    }

    private MemberInformation findMemberById(Long memberId) {
        return memberInformationRepository
                .findById(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));
    }

    @Override
    @Transactional
    public QuestionResponse.Modify modifyQuestionProcessing(Long memberId, Long questionId, QuestionRequest.Modify form) {
        log.info("Modify Question Processing");

        MemberInformation requester = findMemberById(memberId);

        Question question = findQuestionById(questionId);


        question.modify(form.getTitle(), form.getContents());

        eventPublisher.publishEvent(new QuestionEventDto.ModifyEvent(requester.getMemberId(), question.getId(), form.getImageIds()));

        return new QuestionResponse.Modify(question.getId(), question.getModifiedAt());
    }


    @Transactional
    public QuestionResponse.Delete deleteQuestionProcess(Long memberId, Long questionId) {
        log.info("Delete Question Process");
        MemberInformation requester = findMemberById(memberId);

        Question question = findQuestionById(questionId);


        question.changeExposure(DELETE);

        eventPublisher.publishEvent(new QuestionEventDto.DeleteEvent(question));

        return new QuestionResponse.Delete(questionId, question.getDeletedAt());
    }

    private Question findQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION));
    }


}
