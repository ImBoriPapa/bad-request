package com.study.badrequest.api.member;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.service.MemberCommandServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
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
    @Autowired
    MemberCommandServiceImpl memberCommandService;
    @Autowired
    MemberRepository memberRepository;

    private String sampleEmail = "sample@google.com";
    private String sampleContact = "010-1234-1234";

    @BeforeEach
    void beforeEach() {
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("sample@google.com")
                .nickname("sample")
                .password("sample1234!@")
                .contact(sampleContact)
                .build();
        memberCommandService.signupMember(form);
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 요청 검증 테스트 - email 중복")
    void createValidation1() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email(sampleEmail) // 중복된 이매일
                .nickname("tester")
                .password("test1234!@")
                .contact("010-1234-1234")
                .build();
        //when
        mockMvc.perform(post("/api/v1/members")
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
        MemberRequest.CreateMember form = MemberRequest.CreateMember
                .builder()
                .email("new@new.com")
                .nickname("tester")
                .password("password1234!@")
                .contact(sampleContact) //중복된 연락처
                .build();
        //when
        mockMvc.perform(post("/api/v1/members")
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
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("")
                .nickname("tester")
                .password("")
                .contact("")
                .build();
        //when
        mockMvc.perform(post("/api/v1/members")
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

}