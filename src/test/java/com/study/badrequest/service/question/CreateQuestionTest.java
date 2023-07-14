package com.study.badrequest.service.question;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionMetrics;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateQuestionTest extends QuestionServiceTestBase {
    @Test
    @DisplayName("질문 글 생성 실패 테스트: 회원 정보를 찾을 수 없을 경우")
    void test1() throws Exception {
        //given
        Long memberId = 123L;
        String title = "제목입니다.";
        String contents = "내용입니다.";
        List<String> tags = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();
        QuestionRequest.Create request = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> questionService.createQuestionProcessing(memberId, request))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("질문 글 생성 성공 테스트")
    void test2() throws Exception {
        //given
        Long memberId = 123L;
        String title = "제목입니다.";
        String contents = "내용입니다.";
        List<String> tags = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();
        QuestionRequest.Create request = new QuestionRequest.Create(title, contents, tags, imageIds);
        QuestionMetrics questionMetrics = QuestionMetrics.createQuestionMetrics();
        Member member = Member.createWithEmail("email@email.com", "password", "contact");
        Question question = Question.createQuestion(title, contents, member, questionMetrics);
        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(questionRepository.save(any())).willReturn(question);
        questionService.createQuestionProcessing(memberId, request);
        //then
        verify(memberRepository).findById(memberId);
        verify(questionRepository).save(question);
        verify(eventPublisher).publishEvent(new QuestionEventDto.CreateEvent(any(), question, request.getTags(), request.getImageIds()));

    }
}
