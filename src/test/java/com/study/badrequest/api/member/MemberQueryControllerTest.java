package com.study.badrequest.api.member;

import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.study.badrequest.SampleUserData.SAMPLE_PASSWORD;
import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;
import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Slf4j
class MemberQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtLoginService loginService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-1234")
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);

    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Member Info 테스트")
    void memberInfoTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginResponse.LoginDto loginDto = loginService.loginProcessing(email, password);

        //when
        mockMvc.perform(get("/api/v1/members/auth")
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                //then
                .andDo(document("member-auth",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("회원 식별 아이디"),
                                fieldWithPath("result.authority").type(JsonFieldType.STRING).description("회원 권한"),
                                fieldWithPath("result.links").type(JsonFieldType.ARRAY).description("링크 정보"))
//                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 정보"),
//                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크"))
                ));
    }

}