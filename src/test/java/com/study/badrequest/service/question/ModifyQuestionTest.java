package com.study.badrequest.service.question;

import com.study.badrequest.common.response.ApiResponseStatus;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberProfile;
import com.study.badrequest.member.command.domain.ProfileImage;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionMetrics;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ModifyQuestionTest extends QuestionServiceTestBase {

    @Test
    @DisplayName("질문 글 수정 실패 테스트: 질문글 정보를 찾을 수 없을 경우")
    void test1() throws Exception {
        //given
        Long memberId = 123L;
        Long questionId = 1324L;
        QuestionRequest.Modify request = new QuestionRequest.Modify();
        //when
        given(questionRepository.findById(questionId)).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> questionService.modifyQuestionProcessing(memberId, questionId, request))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOT_FOUND_QUESTION.getMessage());
    }

    @Test
    @DisplayName("질문 글 수정 실패 테스트: 수정 요청자의 정보를 찾을 수 없을 경우")
    void test2() throws Exception {
        //given
        Long memberId = 123L;
        Long questionId = 1324L;
        QuestionRequest.Modify request = new QuestionRequest.Modify();
        Member member = Member.createWithEmail("email@email.com", "password", "01012341234",MemberProfile.createMemberProfile("",ProfileImage.createDefaultImage("")));

        Question question = Question.createQuestion("", "", member, QuestionMetrics.createQuestionMetrics());

        //when
        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> questionService.modifyQuestionProcessing(memberId, questionId, request))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(ApiResponseStatus.NOTFOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("질문 글 수정 테스트")
    void test3() throws Exception {
        //given
        Long memberId = 123L;
        Long questionId = 1324L;
        QuestionRequest.Modify request = new QuestionRequest.Modify("New Title", "New Contents", List.of(1L, 2L, 3L));

        Member questioner = Member.createWithEmail("email@email.com", "password", "01012341234",MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));

        Member requester = Member.createWithEmail("email@email.com", "password", "01012341234",MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));

        Question question = Question.createQuestion("title", "contents", questioner, QuestionMetrics.createQuestionMetrics());
        //when
        given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
        given(memberRepository.findById(any())).willReturn(Optional.of(requester));
        questionService.modifyQuestionProcessing(memberId, questionId, request);
        //then
        verify(memberRepository).findById(memberId);
        verify(questionRepository).findById(questionId);


    }
}
