package com.study.badrequest.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.member.dto.MemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.api.member.MemberCommandController.POST_MEMBER_URL;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberIntegrationAndRestDocsTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입")
    void createMember() throws Exception {
        //given
        String email = "bori@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "010-1234-5123";

        MemberRequest.CreateMember request = new MemberRequest.CreateMember(email, password, nickname, contact);

        //then
        mockMvc.perform(post(POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.id").isNumber())
                .andExpect(jsonPath("result.createdAt").isNotEmpty())
                .andExpect(jsonPath("result.links.[0].rel").value("Login"))
                .andExpect(jsonPath("result.links.[0].href").exists())
                .andDo(document("create-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("password").type(STRING).description("비밀번호"),
                                fieldWithPath("nickname").type(STRING).description("닉네임"),
                                fieldWithPath("contact").type(STRING).description("연락처")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("커스텀 응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(STRING).description("커스텀 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("회원 식별 아이디"),
                                fieldWithPath("result.createdAt").type(STRING).description("회원 가입 시간"),
                                fieldWithPath("result.links.[].rel").type(STRING).description("로그인"),
                                fieldWithPath("result.links.[].href").type(STRING).description("로그인 경로")
                        )
                ));

    }

}