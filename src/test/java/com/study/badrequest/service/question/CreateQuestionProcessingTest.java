package com.study.badrequest.service.question;


import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberProfile;
import com.study.badrequest.member.command.domain.ProfileImage;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionMetrics;

import com.study.badrequest.dto.question.QuestionRequest;

import com.study.badrequest.question.command.domain.QuestionEventDto;
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
class CreateQuestionProcessingTest extends QuestionServiceTestBase {

    @Test
    @DisplayName("질문글 생성 실패 테스트1: tag가 최소 1개 이상이 아닐 경우")
    void createQuestionTest1() throws Exception {
        //given
        final Long memberId = 123L;
        final String title = "제목입니다.";
        final String contents = "내용입니다.";

        QuestionRequest.Create form = new QuestionRequest.Create(title, contents, null, new ArrayList<>());
        //when

        //then
        assertThatThrownBy(() -> questionService.createQuestionProcessing(memberId, form))
                .isInstanceOf(CustomRuntimeException.class);

    }

    @Test
    @DisplayName("질문글 생성 실패 테스트2: tag가 최대 5개 이상일 경우")
    void createQuestionTest2() throws Exception {
        //given
        final Long memberId = 123L;
        final String title = "제목입니다.";
        final String contents = "내용입니다.";
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");
        final List<Long> imageIds = null;

        QuestionRequest.Create form = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when

        //then
        assertThatThrownBy(() -> questionService.createQuestionProcessing(memberId, form))
                .isInstanceOf(CustomRuntimeException.class);

    }

    @Test
    @DisplayName("질문글 생성 실패 테스트3: 회원 정보를 찾을 수 없을 경우")
    void createQuestionTest3() throws Exception {
        //given
        final Long memberId = 123L;
        final String title = "제목입니다.";
        final String contents = "내용입니다.";
        final List<String> tags = List.of("tag1", "tag2", "tag3");
        final List<Long> imageIds = null;

        QuestionRequest.Create form = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        given(memberRepository.findById(any())).willReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> questionService.createQuestionProcessing(memberId, form))
                .isInstanceOf(CustomRuntimeException.class);

    }

    @Test
    @DisplayName("질문글 생성 성공 테스트")
    void createQuestionTest() throws Exception {
        //given
        final Long memberId = 123L;
        final String title = "제목입니다.";
        final String contents = "내용입니다.";
        final List<String> tags = List.of("tag1", "tag2", "tag3");
        final List<Long> imageIds = new ArrayList<>();

        Member member = createSampleMember();
        QuestionRequest.Create form = new QuestionRequest.Create(title, contents, tags, imageIds);

        Question question = Question.createQuestion(title, contents, member, QuestionMetrics.createQuestionMetrics());

        //when
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(questionRepository.save(any())).willReturn(question);
        questionService.createQuestionProcessing(memberId, form);
        //then
        verify(memberRepository).findById(memberId);
        verify(questionRepository).save(question);
        verify(eventPublisher).publishEvent(any(QuestionEventDto.CreateEvent.class));

    }


    private Member createSampleMember() {
        final String email = "email@email.com";
        final String password = "password1234";
        final String contact = "01012341234";
        final String nickname = "nickname";
        final String defaultImage = "defaultImage";
        Member member = Member.createWithEmail(email, password, contact,MemberProfile.createMemberProfile(nickname, ProfileImage.createDefaultImage(defaultImage)));
        return member;
    }
}