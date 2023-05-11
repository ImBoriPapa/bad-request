package com.study.badrequest.api_docs;

import com.study.badrequest.api.login.LoginController;
import com.study.badrequest.commons.constants.JwtTokenHeader;
import com.study.badrequest.config.SecurityConfig;
import com.study.badrequest.dto.login.LoginRequest;
import com.study.badrequest.dto.login.LoginResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.service.login.LoginService;
import com.study.badrequest.utils.cookie.CookieFactory;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.jwt.TokenDto;
import com.study.badrequest.utils.modelAssembler.LoginModelAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.commons.constants.ApiURL.LOGIN_URL;
import static com.study.badrequest.commons.constants.JwtTokenHeader.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = LoginController.class)
@Import(LoginModelAssembler.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class LoginRestDocs {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoginService loginService;
    @MockBean
    private JwtUtils jwtUtils;
    @Autowired
    private LoginModelAssembler modelAssembler;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    @DisplayName("bad-request 로그인")
    void 일반로그인() throws Exception {
        //given
        String email = "boriPapa@gmail.com";
        String password = "password1234!@";

        LoginRequest.Login request = new LoginRequest.Login(email, password);

        ResponseCookie tokenCookie = CookieFactory.createRefreshTokenCookie("refresh-token", 604800000L);

        LoginResponse.LoginDto loginDto = new LoginResponse.LoginDto(523L, ACCESS_TOKEN_PREFIX+"access-token", tokenCookie, LocalDateTime.now());

        //when
        given(loginService.emailLoginProcessing(any(), any(), any())).willReturn(loginDto);

        //then
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andDo(document("login-email",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호 - 숫자,문자,특수문자 포함 8~15자리")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token"),
                                headerWithName(HttpHeaders.SET_COOKIE).description("Refresh Token")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.memberId").type(NUMBER).description("회원 식별 아이디"),
                                fieldWithPath("result.loggedIn").type(STRING).description("로그인 시간"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("로그인한 회원 정보 요청"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("url"),
                                fieldWithPath("result.links.[1].rel").type(STRING).description("로그아웃 요청"),
                                fieldWithPath("result.links.[1].href").type(STRING).description("url"),
                                fieldWithPath("result.links.[2].rel").type(STRING).description("토큰 재발급 요청"),
                                fieldWithPath("result.links.[2].href").type(STRING).description("url")
                        )
                ));

    }

    @Test
    @DisplayName("Oauth2 로그인")
    void oauth2LoginTest() throws Exception {
        //given

        //when

        //then

    }
}
