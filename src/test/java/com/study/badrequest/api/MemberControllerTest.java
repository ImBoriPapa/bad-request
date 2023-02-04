package com.study.badrequest.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.domain.Member.dto.MemberRequestForm;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.domain.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginDto;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.SampleData.*;
import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Slf4j
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtLoginService loginService;

    @Test
    @DisplayName("회원가입")
    void 회원가입() throws Exception {
        //given
        MemberRequestForm.CreateMember form = MemberRequestForm.CreateMember.builder()
                .email("tester@test.com")
                .nickname("tester")
                .password("test1234!@")
                .name("테스터")
                .contact("010-1234-1234")
                .build();
        //when
        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.memberId").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.links").exists())
                .andExpect(jsonPath("$.result.links.[0].rel").exists())
                .andExpect(jsonPath("$.result.links.[0].href").exists())
                .andDo(print())
                .andDo(document("postMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("contact").type(JsonFieldType.STRING).description("연락처")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("계정 생성일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 정보"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크"))));
    }

    @Test
    @DisplayName("회원 가입 요청 검증 테스트 - email 중복")
    void createValidation1() throws Exception {
        //given
        MemberRequestForm.CreateMember form = MemberRequestForm.CreateMember.builder()
                .email(SAMPLE_USER_EMAIL)
                .nickname("tester")
                .password("test1234!@")
                .name("테스터")
                .contact("010-1234-1234")
                .build();
        //when
        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("status").value(CustomStatus.DUPLICATE_EMAIL.name()))
                .andExpect(jsonPath("requestPath").exists())
                .andExpect(jsonPath("errorCode").value(CustomStatus.DUPLICATE_EMAIL.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.DUPLICATE_EMAIL.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 가입 요청 검증 테스트 - contact 중복")
    void createValidation2() throws Exception {
        //given
        MemberRequestForm.CreateMember form = MemberRequestForm.CreateMember.builder()
                .email("new@new.com")
                .nickname("tester")
                .password("password1234!@")
                .name("테스터")
                .contact(SAMPLE_USER_CONTACT)
                .build();
        //when
        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("status").value(CustomStatus.DUPLICATE_CONTACT.name()))
                .andExpect(jsonPath("requestPath").exists())
                .andExpect(jsonPath("errorCode").value(CustomStatus.DUPLICATE_CONTACT.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.DUPLICATE_CONTACT.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 가입 요청 검증 테스트 - form validation")
    void createValidation3() throws Exception {
        //given
        MemberRequestForm.CreateMember form = MemberRequestForm.CreateMember.builder()
                .email("")
                .nickname("tester")
                .password("")
                .name("테스터")
                .contact("")
                .build();
        //when
        mockMvc.perform(post("/api/v1/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("status").value(CustomStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("requestPath").exists())
                .andExpect(jsonPath("errorCode").value(CustomStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void putPasswordTest() throws Exception {
        //given
        String password = SAMPLE_PASSWORD;
        String newPassword = "newPassword1234!@";
        MemberRequestForm.ResetPassword resetForm = new MemberRequestForm.ResetPassword(password, newPassword);
        String content = objectMapper.writeValueAsString(resetForm);

        LoginDto loginDto = loginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);

        //when
        mockMvc.perform(put("/api/v1/member/{memberId}/password", loginDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken()))

                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("putPassword",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별 아이디").attributes()
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("기존 비밀번호"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("변경할 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("계정 생성일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("회원 정보"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크"))));
    }

    @Test
    @DisplayName("연락처 변경 테스트")
    void putContactTest() throws Exception {
        //given
        String newContact = "010-1212-1312";
        MemberRequestForm.UpdateContact contact = new MemberRequestForm.UpdateContact(newContact);
        String content = objectMapper.writeValueAsString(contact);

        LoginDto loginDto = loginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);

        //when
        mockMvc.perform(put("/api/v1/member/{memberId}/contact", loginDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken()))

                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("putContact",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별 아이디").attributes()
                        ),
                        requestFields(
                                fieldWithPath("contact").type(JsonFieldType.STRING).description("새 연락처")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("계정 생성일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("로그인"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크"))));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void deleteMemberTest() throws Exception {
        //given
        MemberRequestForm.DeleteMember password = new MemberRequestForm.DeleteMember(SAMPLE_PASSWORD);
        String content = objectMapper.writeValueAsString(password);

        LoginDto loginDto = loginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/member/{memberId}", loginDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken()))

                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별 아이디").attributes()
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.thanks").type(JsonFieldType.STRING).description("인사"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("회원 가입"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크"))));
    }

    @Test
    @DisplayName("이메일 중복확인")
    void duplicateEmailTest() throws Exception{
        //given
        String email = "gdfagsdgs@email.com";

        mockMvc.perform(get("/api/v1/member/email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andDo(document("email_duplicate_success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.duplicate").type(JsonFieldType.BOOLEAN).description("중복 여부"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("요청 이메일")
                        )
                ));
        //when
        String dupl = "user@gmail.com";
        mockMvc.perform(get("/api/v1/member/email")
                        .param("email", dupl)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andDo(document("email_duplicate_fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                fieldWithPath("requestPath").type(JsonFieldType.STRING).description("요청 URI"),
                                fieldWithPath("errorCode").type(JsonFieldType.NUMBER).description("에러 코드"),
                                fieldWithPath("message").type(JsonFieldType.ARRAY).description("에러 메시지")
                        )
                ));


    }
}