package com.study.badrequest.api.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.service.LoginServiceImpl;
import com.study.badrequest.domain.login.dto.LoginRequest;
import com.study.badrequest.domain.login.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
@ActiveProfiles("test")
class LoginControllerTest extends BaseMemberTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    LoginServiceImpl loginServiceImpl;

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



    @Test
    @DisplayName("????????? ?????????")
    void loginTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginRequest.Login form = new LoginRequest.Login(email, password);
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
                                fieldWithPath("email").description("?????????"),
                                fieldWithPath("password").description("????????????")
                        ),
                        responseHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token"),
                                headerWithName("Set-Cookie").description("Refresh Token Cookie")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.accessTokenExpired").type(JsonFieldType.VARIES).description("Access Token ????????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.links.[1].rel").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.links.[1].href").type(JsonFieldType.STRING).description("??????")

                        )
                ));

        //then

    }

    @Test
    @DisplayName("????????? ?????? ?????????-????????? ????????????")
    void ???????????????1() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "wrong!@";
        LoginRequest.Login form = new LoginRequest.Login(email, password);
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
    @DisplayName("????????? ?????? ?????????-????????? ?????????")
    void ???????????????2() throws Exception {
        //given
        String email = "wrong@test.com";
        String password = "password1234!@";

        LoginRequest.Login form = new LoginRequest.Login(email, password);
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
    @DisplayName("???????????? ?????????")
    void logoutTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginResult = loginServiceImpl.login(email, password);
        //logout ??????
        mockMvc.perform(post("/api/v1/log-out")
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginResult.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.logout").value(true))
                .andExpect(jsonPath("result.logoutAt").exists())
                .andDo(print())
                .andDo(document("logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("result.logout").description("???????????? ??????"),
                                fieldWithPath("result.logoutAt").description("???????????? ??????"),
                                fieldWithPath("result.links.[0].rel").description("?????????"),
                                fieldWithPath("result.links.[0].href").description("??????")
                        )


                ));
    }

    @Test
    @DisplayName("???????????? ??? ??????")
    void afterLogout() throws Exception {
        //logout ??? ??????
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginResult = loginServiceImpl.login(email, password);
        loginServiceImpl.logout(loginResult.getAccessToken());
        mockMvc.perform(post("/test/welcome")
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginResult.getAccessToken()))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andDo(document("accessAfterLogout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("requestPath").type(JsonFieldType.STRING).description("?????? URL"),
                                fieldWithPath("errorCode").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.ARRAY).description("?????? ?????????"))));
    }

    @Test
    @DisplayName("??????????????? ?????????")
    void reIssuedTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginResponse.LoginDto member = loginServiceImpl.login(email, password);

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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("Member ?????? ?????????"),
                                fieldWithPath("result.accessTokenExpired").type(JsonFieldType.STRING).description("Access Token ?????? ??????"),
                                fieldWithPath("result.links").ignored()
                        )
                ));
    }

    @Test
    @DisplayName("?????? ????????? ??????1 - ????????? AccessToken")
    void failReissue() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginResponse.LoginDto member = loginServiceImpl.login(email, password);
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
    @DisplayName("?????? ????????? ??????2 - ????????? RefreshToken")
    void failReissue2() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginResponse.LoginDto member = loginServiceImpl.login(email, password);
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
    @DisplayName("?????? ????????? ??????3 - ????????? Refresh ????????? ?????? ??????")
    void failReissue3() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto member = loginServiceImpl.login(email, password);
        //when
        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + member.getAccessToken())
                ).andExpect(status().isBadRequest())
                .andDo(print());
        //then
    }

    @Test
    @DisplayName("?????? ?????????- ??????")
    void authorityFailTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginResponse.LoginDto member = loginServiceImpl.login(email, password);

        //?????? ??????
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ??????"),
                                fieldWithPath("requestPath").type(JsonFieldType.STRING).description("?????? URL"),
                                fieldWithPath("errorCode").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.ARRAY).description("?????? ?????????")
                        )
                ));

    }


}