package com.study.badrequest.login.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.domain.Member.domain.entity.Member;
import com.study.badrequest.domain.Member.domain.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.domain.repository.RefreshTokenRepository;
import com.study.badrequest.domain.login.domain.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginDto;
import com.study.badrequest.domain.login.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_COOKIE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    JwtLoginService loginService;

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() throws Exception {
        //given
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.MEMBER).build();
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
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token"),
                                headerWithName("Set-Cookie").description("Refresh Token Cookie")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.accessTokenExpired").type(JsonFieldType.VARIES).description("Access Token 만료기한"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("로그 아웃"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크"),
                                fieldWithPath("result.links.[1].rel").type(JsonFieldType.STRING).description("토큰 재발급"),
                                fieldWithPath("result.links.[1].href").type(JsonFieldType.STRING).description("링크")

                        )
                ));

        //then

    }

    @Test
    @DisplayName("로그인 실패 테스트-잘못된 비밀번호")
    void 로그인실패1() throws Exception {
        //given
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.MEMBER).build();
        memberRepository.save(member);
        LoginRequest.Login form = new LoginRequest.Login("email@email.com", "wrong-password");
        String content = objectMapper.writeValueAsString(form);
        //when
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
        //then
    }

    @Test
    @DisplayName("로그인 실패 테스트-잘못된 이메일")
    void 로그인실패2() throws Exception {
        //given
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.MEMBER).build();
        memberRepository.save(member);
        LoginRequest.Login form = new LoginRequest.Login("wrong@email.com", "password1234!@");
        String content = objectMapper.writeValueAsString(form);
        //when
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
        //then
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        //given
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.MEMBER).build();
        memberRepository.save(member);
        LoginDto loginDto = loginService.loginProcessing("email@email.com", "password1234!@");


        //logout 요청
        mockMvc.perform(post("/api/v1/log-out")
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 메시지"),
                                fieldWithPath("result.thanks").description("인삿말"),
                                fieldWithPath("result.links.[0].rel").description("로그인"),
                                fieldWithPath("result.links.[0].href").description("링크")
                        )


                ));
    }

    @Test
    @DisplayName("로그아웃 후 요청")
    void afterLogout() throws Exception {
        //logout 후 접근
        Member member = Member.createMember()
                .email("email@email.com")
                .password(passwordEncoder.encode("password1234!@"))
                .authority(Member.Authority.MEMBER).build();
        memberRepository.save(member);
        LoginDto loginDto = loginService.loginProcessing("email@email.com", "password1234!@");
        loginService.logoutProcessing(loginDto.getAccessToken());
        mockMvc.perform(post("/test/welcome")
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken()))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andDo(document("accessAfterLogout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 상태"),
                                fieldWithPath("requestPath").type(JsonFieldType.STRING).description("요청 URL"),
                                fieldWithPath("errorCode").type(JsonFieldType.NUMBER).description("커스텀 에러 코드"),
                                fieldWithPath("message").type(JsonFieldType.ARRAY).description("에러 메시지"))));
    }

    @Test
    @DisplayName("토큰재발급 테스트")
    void reIssuedTest() throws Exception {
        //given
        LoginDto member = loginService.loginProcessing("user@gmail.com", "password1234!@");
        ResponseCookie refreshCookie = member.getRefreshCookie();

        Cookie cookie = new Cookie(refreshCookie.getName(), refreshCookie.getValue());
        cookie.setPath(refreshCookie.getPath());
        cookie.setMaxAge(refreshCookie.getMaxAge().toMillisPart());
        cookie.setSecure(refreshCookie.isSecure());
        cookie.setHttpOnly(refreshCookie.isHttpOnly());
        //when
        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + member.getAccessToken())
                        .cookie(cookie)
                ).andDo(print())
                //then
                .andDo(document("reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 메시지"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("Member 식별 아이디"),
                                fieldWithPath("result.accessTokenExpired").type(JsonFieldType.STRING).description("Access Token 만료 기한"),
                                fieldWithPath("result.links").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급 실패1 - 잘못된 AccessToken")
    void failReissue() throws Exception {
        //given
        LoginDto member = loginService.loginProcessing("user@gmail.com", "password1234!@");
        ResponseCookie refreshCookie = member.getRefreshCookie();
        Cookie cookie = new Cookie(refreshCookie.getName(), refreshCookie.getValue());
        cookie.setPath(refreshCookie.getPath());
        cookie.setMaxAge(refreshCookie.getMaxAge().toMillisPart());
        cookie.setSecure(refreshCookie.isSecure());
        cookie.setHttpOnly(refreshCookie.isHttpOnly());
        //when
        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + member.getAccessToken() + "wrong token")
                        .cookie(cookie)
                ).andExpect(status().isUnauthorized())
                .andDo(print());
        //then

    }

    @Test
    @DisplayName("토큰 재발급 실패2 - 잘못된 RefreshToken")
    void failReissue2() throws Exception {
        //given
        LoginDto member = loginService.loginProcessing("user@gmail.com", "password1234!@");
        ResponseCookie refreshCookie = member.getRefreshCookie();
        Cookie cookie = new Cookie(refreshCookie.getName(), refreshCookie.getValue() + "wrong");
        cookie.setPath(refreshCookie.getPath());
        cookie.setMaxAge(refreshCookie.getMaxAge().toMillisPart());
        cookie.setSecure(refreshCookie.isSecure());
        cookie.setHttpOnly(refreshCookie.isHttpOnly());
        //when
        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + member.getAccessToken())
                        .cookie(cookie)
                ).andExpect(status().isUnauthorized())
                .andDo(print());
        //then
    }

    @Test
    @DisplayName("토큰 재발급 실패3 - 잘못된 Refresh 쿠키가 없을 경우")
    void failReissue3() throws Exception {
        //given
        LoginDto member = loginService.loginProcessing("user@gmail.com", "password1234!@");
        //when
        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + member.getAccessToken())
                ).andExpect(status().isBadRequest())
                .andDo(print());
        //then
    }

    @Test
    @DisplayName("인가 테스트- 실패")
    void authorityFailTest() throws Exception {
        //given
        LoginDto member = loginService.loginProcessing("user@gmail.com", "password1234!@");

        //인가 없음
        mockMvc.perform(get("/test/teacher")
                        .header(AUTHORIZATION_HEADER, "Bearer " + member.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("status").value(CustomStatus.PERMISSION_DENIED.name()))
                .andDo(print())
                .andDo(document("authorityFail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION_HEADER).description("AccessToken")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 상태"),
                                fieldWithPath("requestPath").type(JsonFieldType.STRING).description("요청 URL"),
                                fieldWithPath("errorCode").type(JsonFieldType.NUMBER).description("커스텀 에러 코드"),
                                fieldWithPath("message").type(JsonFieldType.ARRAY).description("에러 메시지")
                        )
                ));

    }

    @Test
    @DisplayName("인가 테스트- 성공")
    void authoritySuccessTest1() throws Exception {
        //given
        LoginDto teacher = loginService.loginProcessing("teacher@gmail.com", "password1234!@");

        //when
        //인가 있음
        mockMvc.perform(get("/test/teacher")
                        .header(AUTHORIZATION_HEADER, "Bearer " + teacher.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.authority").exists())
                .andDo(print())
                .andDo(document("authoritySuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION_HEADER).description("AccessToken")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("결과"),
                                fieldWithPath("result.authority").type(JsonFieldType.STRING).description("결과 예시")
                        )
                ));

        //then

    }

    @Test
    @DisplayName("인가 테스트- 성공 하위 권한 접근")
    void authoritySuccessTest2() throws Exception {
        //given
        LoginDto admin = loginService.loginProcessing("admin@gmail.com", "password1234!@");
        //when
        //인가 포함
        mockMvc.perform(get("/test/teacher")
                        .header(AUTHORIZATION_HEADER, "Bearer " + admin.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.authority").exists())
                .andDo(print())
                .andDo(document("authoritySuccessAdmin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION_HEADER).description("AccessToken")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 메시지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT).description("결과"),
                                fieldWithPath("result.authority").type(JsonFieldType.STRING).description("결과 예시")
                        )
                ));
    }


}