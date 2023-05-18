package com.study.badrequest.service.answer;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.answer.Answer;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.dto.answer.AnswerRequest;
import com.study.badrequest.dto.answer.AnswerResponse;
import com.study.badrequest.event.answer.AnswerEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.answer.AnswerRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_FOUND_ANSWER;

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
    public AnswerResponse.Register registerAnswer(Long memberId, Long questionId, AnswerRequest.Register form) {
        log.info("답변 등록\n 요청 회원 아이디: {}\n 질문 아이디: {}\n 답변 내용: {}",memberId,questionId,form.getContents().substring(0,10)+"....");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOT_FOUND_QUESTION));

        Answer answer = Answer.createAnswer()
                .contents(form.getContents())
                .question(question)
                .member(member)
                .build();

        Answer saved = answerRepository.save(answer);

        eventPublisher.publishEvent(new AnswerEventDto.Register(saved,member));

        return new AnswerResponse.Register(saved.getId(), saved.getAnsweredAt());
    }

    @Override
    @Transactional
    public AnswerResponse.Modify modifyAnswer(Long memberId, Long answerId, AnswerRequest.Modify form) {
        log.info("답변 수정");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));

        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_ANSWER));

        answer.updateContents(form.getContents());

        return new AnswerResponse.Modify(answer.getId(), answer.getModifiedAt());
    }

    @Override
    @Transactional
    public AnswerResponse.Delete deleteAnswer(Long memberId, Long answerId) {
        log.info("답변 삭제");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomRuntimeException(ApiResponseStatus.NOTFOUND_MEMBER));

        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new CustomRuntimeException(NOT_FOUND_ANSWER));
        answer.statusToDelete();

        return new AnswerResponse.Delete(answer.getDeletedAt());
    }


}
