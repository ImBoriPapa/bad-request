package com.study.badrequest.service.question;

import com.study.badrequest.hashtag.command.domain.HashTag;
import com.study.badrequest.member.command.domain.Member;
import com.study.badrequest.member.command.domain.MemberProfile;
import com.study.badrequest.member.command.domain.ProfileImage;
import com.study.badrequest.question.command.domain.Question;
import com.study.badrequest.question.command.domain.QuestionMetrics;
import com.study.badrequest.question.command.domain.QuestionTag;
import com.study.badrequest.common.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateQuestionTagServiceTest extends QuestionTagServiceTestBase {

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
    @DisplayName("질문글 테그 생성 성공 테스트1: 태그가 전부 신규일 경우")
    void test6() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");
        Member member = createSampleMember();
        Question question = Question.createQuestion("title", "contents", member, QuestionMetrics.createQuestionMetrics());

        List<QuestionTag> questionTags = createSampleQuestionTags(tags, question);
        //when
        given(questionRepository.findById(any())).willReturn(Optional.of(question));
        given(hashTagRepository.findAllByHashTagNameIn(any())).willReturn(Collections.emptyList());
        given(questionTagRepository.saveAll(any())).willReturn(questionTags);
        questionTagService.createQuestionTagProcessing(questionId, tags);
        //then
        verify(questionRepository).findById(questionId);
        verify(hashTagRepository).findAllByHashTagNameIn(any());
        verify(questionTagRepository).saveAll(any());
    }

    @Test
    @DisplayName("질문글 테그 생성 성공 테스트2: 태그가 전부 신규3, 기존2")
    void test7() throws Exception {
        //given
        final Long questionId = 123L;
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");
        final List<HashTag> existsHashTags = createExistsHashTags();

        Member member = createSampleMember();
        Question question = Question.createQuestion("title", "contents", member, QuestionMetrics.createQuestionMetrics());

        List<QuestionTag> questionTags = createSampleQuestionTags(tags, question);
        //when
        given(questionRepository.findById(any())).willReturn(Optional.of(question));
        given(hashTagRepository.findAllByHashTagNameIn(any())).willReturn(existsHashTags);
        given(questionTagRepository.saveAll(any())).willReturn(questionTags);
        questionTagService.createQuestionTagProcessing(questionId, tags);
        //then
        verify(questionRepository).findById(questionId);
        verify(hashTagRepository).findAllByHashTagNameIn(any());
        verify(questionTagRepository).saveAll(any());
    }

    private List<HashTag> createExistsHashTags() {
        HashTag hashTag4 = HashTag.createHashTag("#tag4");
        HashTag hashTag5 = HashTag.createHashTag("#tag5");
        return List.of(hashTag4, hashTag5);
    }


    private List<QuestionTag> createSampleQuestionTags(List<String> tags, Question question) {
        List<HashTag> hashTags = createSampleHashTags(tags);

        List<QuestionTag> questionTags = new ArrayList<>();
        for (HashTag hashTag : hashTags) {
            questionTags.add(QuestionTag.createQuestionTag(question, hashTag));
        }
        return questionTags;
    }


    private List<HashTag> createSampleHashTags(List<String> tags) {
        List<HashTag> hashTags = new ArrayList<>();
        for (String tag : tags) {
            hashTags.add(HashTag.createHashTag("#" + tag));
        }
        return hashTags;
    }


    private Member createSampleMember() {
        return Member.createWithEmail("email@email.com", "password1234!@", "01011111234", MemberProfile.createMemberProfile("nickname", ProfileImage.createDefaultImage("image")));
    }
}