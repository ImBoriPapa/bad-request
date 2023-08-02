package com.study.badrequest.answer.command.application;

import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.answer.command.domain.Answer;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.dto.answer.AnswerRequest;
import com.study.badrequest.dto.answer.AnswerResponse;
import com.study.badrequest.event.answer.AnswerEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.answer.AnswerRepository;
import com.study.badrequest.member.command.domain.MemberRepository;
import com.study.badrequest.question.command.domain.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_ALLOW_EMPTY_ANSWER;
import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_FOUND_ANSWER;
import static com.study.badrequest.utils.authority.AuthorityUtils.verifyPermission;
import static com.study.badrequest.utils.verification.WordValidateUtils.findBannedWord;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AnswerResponse.Register createAnswer(Long memberId, Long questionId, AnswerRequest.Register form) {
        log.info("Create Answer Start");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_QUESTION));

        if (!StringUtils.hasLength(form.getContents())) {
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_ALLOW_EMPTY_ANSWER);
        }

        findBannedWord(form.getContents());

        Answer answer = Answer.createAnswer()
                .contents(form.getContents())
                .question(question)
                .member(member)
                .build();

        Answer saved = answerRepository.save(answer);

        eventPublisher.publishEvent(new AnswerEventDto.Register(saved, member));

        return new AnswerResponse.Register(saved.getId(), saved.getAnsweredAt());
    }

    @Override
    @Transactional
    public AnswerResponse.Modify modifyAnswer(Long memberId, Long answerId, AnswerRequest.Modify form) {
        log.info("Modify Answer");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_ANSWER));

        verifyPermission(answer.getMember().getId(), member.getId(), member.getAuthority(), NOT_ALLOW_MODIFY_ANSWER);

        answer.modifyContents(form.getContents());

        return new AnswerResponse.Modify(answer.getId(), answer.getModifiedAt());
    }

    @Override
    @Transactional
    public AnswerResponse.Delete deleteAnswer(Long memberId, Long answerId) {
        log.info("Delete Answer");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOTFOUND_MEMBER));

        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_ANSWER));

        verifyPermission(answer.getMember().getId(), member.getId(), member.getAuthority(), NOT_ALLOW_DELETE_ANSWER);

        answer.statusToDelete();

        return new AnswerResponse.Delete(answer.getDeletedAt());
    }


}
