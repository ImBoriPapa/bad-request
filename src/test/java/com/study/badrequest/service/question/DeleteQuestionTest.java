package com.study.badrequest.service.question;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionMetrics;
import com.study.badrequest.event.question.QuestionEventDto;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteQuestionTest extends QuestionServiceTestBase {

    @Test
    @DisplayName("질문 삭제 요청 실패 테스트: 회원 정보를 찾을 수 없을 경우")
    void test1() throws Exception {
        //given
        Long memberId = 123L;
        Long questionId = 1234L;
        //when
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> questionService.deleteQuestionProcess(memberId, questionId))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("질문 삭제 요청 실패 테스트: 질문 정보를 찾을 수 없을 경우")
    void test2() throws Exception {
        //given
        Long memberId = 123L;
        Long questionId = 1234L;
        //when
        given(memberRepository.findById(memberId)).willReturn(Optional.of(mock(Member.class)));
        given(questionRepository.findById(questionId)).willReturn(Optional.empty());
        //then
        Assertions.assertThatThrownBy(() -> questionService.deleteQuestionProcess(memberId, questionId))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOT_FOUND_QUESTION.getMessage());
    }

    @Test
    @DisplayName("질문 삭제 요청 테스트")
    void test3() throws Exception{
        //given
        Long memberId = 123L;
        Long questionId = 1234L;
        Member member = Member.createMemberWithEmail("email@email.com", "password", "01012341234", new MemberProfile("nickname", ProfileImage.createDefaultImage("")));
        Question question = Question.createQuestion("title", "contents", member, QuestionMetrics.createQuestionMetrics());
        //when
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        questionService.deleteQuestionProcess(memberId, questionId);
        //then
        verify(memberRepository).findById(memberId);
        verify(questionRepository).findById(questionId);
        verify(eventPublisher).publishEvent(new QuestionEventDto.DeleteEvent(any()));

    }
}
