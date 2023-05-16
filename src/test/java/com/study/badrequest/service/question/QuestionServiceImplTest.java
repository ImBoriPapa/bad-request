package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.repository.board.HashTagRepository;
import com.study.badrequest.repository.member.MemberRepository;
import com.study.badrequest.repository.question.QuestionRepository;
import com.study.badrequest.repository.question.QuestionTagRepository;
import com.study.badrequest.repository.reommendation.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Slf4j
class QuestionServiceImplTest {

    @InjectMocks
    QuestionServiceImpl questionService;
    @Mock
    QuestionRepository questionRepository;
    @Mock
    HashTagRepository hashTagRepository;
    @Mock
    QuestionTagRepository questionTagRepository;
    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("질문 생성 테스트1: 태그가 1개 이하, 5개 이상")
    void 생성테스트1() throws Exception {
        //given
        Long memberId = 341L;
        String title = "제목입니다.";
        String contents = "내용입니다.";
        List<String> emptyTags = List.of();
        List<Long> emptyLong = List.of();
        List<String> sixTags = List.of("tag1","tag2","tag3","tag4","tag5","tag6");

        //when
        QuestionRequest.Create lessThanOne = new QuestionRequest.Create(title,contents,emptyTags,emptyLong);
        QuestionRequest.Create moreThanFive = new QuestionRequest.Create(title,contents,sixTags,emptyLong);

        //then
        assertThatThrownBy(() -> questionService.createQuestion(memberId, lessThanOne)).isInstanceOf(CustomRuntimeException.class);
        assertThatThrownBy(() -> questionService.createQuestion(memberId, moreThanFive)).isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    @DisplayName("질문 생성 테스트2: 회원 정보를 찾지 못할때")
    void 생성테스트2() throws Exception{
        //given
        Long memberId = 341L;
        String title = "제목입니다.";
        String contents = "내용입니다.";
        List<String> tags = List.of("tag1","tag2","tag3");
        List<Long> emptyLong = List.of();
        QuestionRequest.Create request = new QuestionRequest.Create(title,contents,tags,emptyLong);
        //when
        when(memberRepository.findById(any())).thenThrow(CustomRuntimeException.class);
        //then
        assertThatThrownBy(() -> questionService.createQuestion(memberId, request)).isInstanceOf(CustomRuntimeException.class);
    }

    @Test
    @DisplayName("질문 생성 테스트3: 회원 정보를 찾지 못할때")
    void 생성테스트3() throws Exception{
        //given
        Long memberId = 341L;
        String title = "제목입니다.";
        String contents = "내용입니다.";
        List<String> tags = List.of("tag1","tag2","tag3");
        List<Long> emptyLong = List.of();
        QuestionRequest.Create request = new QuestionRequest.Create(title,contents,tags,emptyLong);
        QuestionResponse.Create response = new QuestionResponse.Create();
        Member member = Member.builder()
                .email("email@email.com")
                .build();

        Question question = Question.createQuestion()
                .member(member)
                .title(title)
                .contents(contents)
                .build();


        //when
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(questionRepository.save(any())).thenReturn(question);
        QuestionResponse.Create create = questionService.createQuestion(memberId, request);
        //then

    }


}