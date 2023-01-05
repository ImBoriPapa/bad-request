package com.study.badrequest.login.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.Member.domain.entity.Member;
import com.study.badrequest.Member.domain.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.login.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_COOKIE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Slf4j
@Transactional
@ActiveProfiles("dev")
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() throws Exception {
        //given
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.USER).build();
        memberRepository.save(member);

        LoginRequest.Login form = new LoginRequest.Login("email@email.com", "password1234!@");
        String content = objectMapper.writeValueAsString(form);
        //when
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION_HEADER))
                .andExpect(cookie().exists(REFRESH_TOKEN_COOKIE))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.memberId").exists())
                .andExpect(jsonPath("$.result.accessTokenExpired").exists())
                .andExpect(jsonPath("$.result.links").exists())
                .andExpect(jsonPath("$.result.links.[0].rel").exists())
                .andExpect(jsonPath("$.result.links.[0].href").exists())
                .andDo(print())
                .andDo(document("login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token").optional(),
                                headerWithName("Set-Cookie").description("Refresh Token Cookie").optional()
                        ),
                        responseFields(
                                fieldWithPath("status").description("커스텀 응답상태"),
                                fieldWithPath("code").description("커스텀 응답 코드"),
                                fieldWithPath("message").description("커스텀 응답 메시지"),
                                fieldWithPath("result.memberId").description("식별 아이디"),
                                fieldWithPath("result.accessTokenExpired").description("Access Token 만료기한"),
                                fieldWithPath("result.links.[0].rel").description("로그 아웃"),
                                fieldWithPath("result.links.[0].href").description("링크"),
                                fieldWithPath("result.links.[1].rel").description("토큰 재발급"),
                                fieldWithPath("result.links.[1].href").description("링크")

                        )
                ));

        //then

    }
}