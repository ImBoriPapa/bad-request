package com.study.badrequest.api.member;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.commons.consts.CustomStatus;

import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.service.MemberCommandServiceImpl;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import com.study.badrequest.utils.validator.MemberValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 단위테스트를 하기에는 너무 많은 의존성을 가지고 있음
 */
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberCommendControllerTest extends BaseMemberTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtLoginService loginService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    private final String sampleEmail = "sample@google.com";
    private final String sampleNickname = "nickname";
    private final String sampleContact = "010-1234-1234";
    private final String samplePassword = "password1234";

    @BeforeEach
    void beforeEach() {
        Member testMember = Member.createMember()
                .email(sampleEmail)
                .nickname(sampleNickname)
                .password(passwordEncoder.encode(samplePassword))
                .contact(sampleContact)
                .authority(Authority.MEMBER)
                .profileImage(ProfileImage.createProfileImage().fullPath("imagePath").build())
                .build();
        memberRepository.save(testMember);
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