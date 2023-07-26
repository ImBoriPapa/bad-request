package com.study.badrequest.service.question;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.question.Question;
import com.study.badrequest.domain.question.QuestionMetrics;
import com.study.badrequest.exception.CustomRuntimeException;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CreateQuestionTagTest extends QuestionTagTestBase {

    @Test
    @DisplayName("질문글 테그 생성 살패 테스트: tags 가 EmptyList")
    void test1() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> emptyTags = Collections.emptyList();
        //when

        //then
        assertThatThrownBy(() -> questionTagService.createQuestionTagProcessing(questionId, emptyTags))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED.getMessage());
    }

    @Test
    @DisplayName("질문글 테그 생성 살패 테스트: tags 가 null")
    void test2() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> nullTags = null;
        //when

        //then
        assertThatThrownBy(() -> questionTagService.createQuestionTagProcessing(questionId, nullTags))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(AT_LEAST_ONE_TAG_MUST_BE_USED_AND_AT_MOST_FIVE_TAGS_MUST_BE_USED.getMessage());
    }

    @Test
    @DisplayName("질문글 테그 생성 살패 테스트: 게시글 정보를 찾을 수 없을 경우")
    void test3() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");

        //when
        given(questionRepository.findById(any())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> questionTagService.createQuestionTagProcessing(questionId, tags))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(NOT_FOUND_QUESTION.getMessage());
    }

    @Test
    @DisplayName("질문글 테그 생성 살패 테스트: 게시글 정보를 찾을 수 없을 경우")
    void test4() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");

        //when
        given(questionRepository.findById(any())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> questionTagService.createQuestionTagProcessing(questionId, tags))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(NOT_FOUND_QUESTION.getMessage());
    }

    @Test
    @DisplayName("질문글 테그 생성 살패 테스트: 게시글 정보를 찾을 수 없을 경우")
    void test5() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");

        //when
        given(questionRepository.findById(any())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> questionTagService.createQuestionTagProcessing(questionId, tags))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(NOT_FOUND_QUESTION.getMessage());
    }



    @Test
    @DisplayName("질문글 테그 생성 살패 테스트: 게시글 정보를 찾을 수 없을 경우")
    void test() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");
        Member member = createSampleMember();
        Question question = Question.createQuestion("title", "contents", member, QuestionMetrics.createQuestionMetrics());
        //when
        given(questionRepository.findById(any())).willReturn(Optional.of(question));

        //then

    }

    @NotNull
    private static Member createSampleMember() {
        return Member.createWithEmail("email@email.com", "password1234!@", "01011111234");
    }
}