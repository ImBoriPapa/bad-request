package com.study.badrequest.api.question;

import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;

import com.study.badrequest.testHelper.WithCustomMockUser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.ResultActions;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.study.badrequest.common.constants.ApiURL.QUESTION_BASE_URL;
import static com.study.badrequest.common.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.common.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static com.study.badrequest.member.command.domain.Authority.MEMBER;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class QuestionCreateApiTest extends QuestionApiTestBase {

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error 제목이 null")
    void 질문생성실패1() throws Exception {
        //given
        final String title = null;
        final String contents = "내용입니다. ----- * 내용은 * 마크다운형식입니다.";
        final List<String> tags = List.of("Java", "Spring");
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error 제목이 공백")
    void 질문생성실패2() throws Exception {
        //given
        final String title = " ";
        final String contents = "내용입니다. ----- * 내용은 * 마크다운형식입니다.";
        final List<String> tags = List.of("Java", "Spring");
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error contents 가 null")
    void 질문생성실패3() throws Exception {
        //given
        final String title = "제목입니다.";
        final String contents = null;
        final List<String> tags = List.of("Java", "Spring");
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error contents 가 5글자 이하")
    void 질문생성실패4() throws Exception {
        //given
        final String title = "제목입니다.";
        final String contents = "1234";
        final List<String> tags = List.of("Java", "Spring");
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error tags 가 null")
    void 질문생성실패5() throws Exception {
        //given
        final String title = "제목입니다.";
        final String contents = "내용입니다.";
        final List<String> tags = null;
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error tag 가 empty")
    void 질문생성실패6() throws Exception {
        //given
        final String title = "제목입니다.";
        final String contents = "내용입니다.";
        final List<String> tags = Collections.emptyList();
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("createQuestion API 실패 테스트: validation error tag 가 5개 이상")
    void 질문생성실패7() throws Exception {
        //given
        final String title = "제목입니다.";
        final String contents = "내용입니다.";
        final List<String> tags = List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6");
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();
        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        //when
        ResultActions actions = perform(accessToken, create);
        //then
        actions.andExpect(status().is(VALIDATION_ERROR.getHttpStatus().value()))
                .andExpect(jsonPath("status").value(VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }


    @Test
    @DisplayName("createQuestion API 성공 테스트")
    @WithCustomMockUser(memberId = "321312", authority = MEMBER)
    void 질문생성성공() throws Exception {
        //given
        final Long questionId = 424211L;
        final String title = "제목입니다.";
        final String contents = "내용입니다. ----- * 내용은 * 마크다운형식입니다.";
        final List<String> tags = List.of("Java", "Spring");
        final List<Long> imageIds = List.of(53L, 34L, 53L);
        final String accessToken = UUID.randomUUID().toString();

        QuestionRequest.Create create = new QuestionRequest.Create(title, contents, tags, imageIds);
        QuestionResponse.Create response = new QuestionResponse.Create(questionId, LocalDateTime.now());
        //when
        when(questionService.createQuestionProcessing(any(), any())).thenReturn(response);
        ResultActions actions = perform(accessToken, create);
        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value(SUCCESS.name()))
                .andExpect(jsonPath("code").value(SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(SUCCESS.getMessage()))
                .andExpect(jsonPath("result.id").value(questionId))
                .andExpect(jsonPath("result.askedAt").exists())
                .andExpect(jsonPath("result.links").exists())
                .andDo(print())
                .andDo(writeAPIDocs());

    }

    private RestDocumentationResultHandler writeAPIDocs() {
        return document("question-create",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(headerWithName(AUTHORIZATION_HEADER).description("Access Token")),
                responseHeaders(headerWithName(LOCATION).description("Resource Location")),
                requestFields(
                        fieldWithPath("title").type(STRING).description("질문 제목"),
                        fieldWithPath("contents").type(STRING).description("질문 내용"),
                        fieldWithPath("tags").type(ARRAY).description("태그"),
                        fieldWithPath("imageIds").type(ARRAY).description("업로드된 이미지 식별 아이디").optional()
                ),
                responseFields(
                        fieldWithPath("status").type(STRING).description("응답 상태"),
                        fieldWithPath("code").type(NUMBER).description("응답 코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("result.id").type(NUMBER).description("질문 식별 아이디"),
                        fieldWithPath("result.askedAt").type(STRING).description("질문 생성 시간"),
                        fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                        fieldWithPath("result.links.[0].href").type(STRING).description("uri")
                )
        );
    }

    private ResultActions perform(String accessToken, QuestionRequest.Create create) throws Exception {
        return mockMvc.perform(post(QUESTION_BASE_URL)
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

    }
}