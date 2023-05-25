package com.study.badrequest.service.answer;

import com.study.badrequest.domain.answer.Answer;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.dto.answer.AnswerRequest;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.answer.AnswerRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AnswerServiceImplTest {

    @InjectMocks
    private AnswerServiceImpl answerService;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    @DisplayName("답변 등록: 정상")
    void registerAnswerTest1() throws Exception {
        //given
        Long memberId = 41L;
        Long questionId = 2154L;
        String contents = "답변 내용입니다.";
        Member member = Member.createSelfRegisteredMember("email@email.com", "password1234", "01011111234", new MemberProfile("nickname", ProfileImage.createDefault("default")));

        Question question = Question.createQuestion()
                .title("제목입니다.")
                .contents("내용입니다.")
                .member(member)
                .build();
        AnswerRequest.Register form = new AnswerRequest.Register(contents, null);

        Answer answer = Answer.createAnswer()
                .member(member)
                .question(question)
                .contents("내용입니다.")
                .build();
        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(questionRepository.findById(any())).thenReturn(Optional.of(question));
        when(answerRepository.save(any())).thenReturn(answer);
        answerService.createAnswer(memberId, questionId, form);

        //then
        verify(memberRepository).findById(memberId);
        verify(questionRepository).findById(questionId);
        verify(answerRepository).save(answer);
    }
    @Test
    @DisplayName("답변 등록: 질문 정보를 찾을 수 없을 때")
    void registerAnswerTest2() throws Exception {
        //given
        Long memberId = 41L;
        Long questionId = 2154L;
        String contents = "답변 내용입니다.";
        Member member = Member.createSelfRegisteredMember("email@email.com", "password1234", "01011111234", new MemberProfile("nickname", ProfileImage.createDefault("default")));

        Question question = Question.createQuestion()
                .title("제목입니다.")
                .contents("내용입니다.")
                .member(member)
                .build();
        AnswerRequest.Register form = new AnswerRequest.Register(contents, null);

        Answer answer = Answer.createAnswer()
                .member(member)
                .question(question)
                .contents("내용입니다.")
                .build();
        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(questionRepository.findById(any())).thenThrow(CustomRuntimeException.class);

        //then
        Assertions.assertThatThrownBy(() -> answerService.createAnswer(memberId, questionId, form))
                .isInstanceOf(CustomRuntimeException.class);
        verify(memberRepository).findById(memberId);
        verify(questionRepository).findById(questionId);
    }

    @Test
    @DisplayName("답변 등록: 회원 정보를 찾을 수 없을 때")
    void registerAnswerTest3() throws Exception {
        //given
        Long memberId = 41L;
        Long questionId = 2154L;
        String contents = "답변 내용입니다.";
        AnswerRequest.Register form = new AnswerRequest.Register(contents, null);
        //when
        when(memberRepository.findById(any())).thenThrow(CustomRuntimeException.class);
        //then
        Assertions.assertThatThrownBy(() -> answerService.createAnswer(memberId, questionId, form))
                .isInstanceOf(CustomRuntimeException.class);
        verify(memberRepository).findById(memberId);
    }



}