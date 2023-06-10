package com.study.badrequest.api_docs;

import com.study.badrequest.api.login.LoginController;
import com.study.badrequest.domain.member.Member;
import com.study.badrequest.domain.member.MemberProfile;
import com.study.badrequest.domain.member.ProfileImage;
import com.study.badrequest.dto.login.LoginRequest;
import com.study.badrequest.dto.login.LoginResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.service.login.LoginService;
import com.study.badrequest.utils.jwt.JwtUtils;
import com.study.badrequest.utils.modelAssembler.LoginModelAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.constants.AuthenticationHeaders.*;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = LoginController.class)
@Import(LoginModelAssembler.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class LoginApiDocs {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoginService loginService;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("bad-request 로그인")
    void 일반로그인() throws Exception {
        //given
        String email = "boriPapa@gmail.com";
        String password = "password1234!@";

        LoginRequest.Login request = new LoginRequest.Login(email, password);

        ResponseCookie tokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN_PREFIX + "RefreshToken")
                .maxAge(604800)
                .secure(true)
                .sameSite("none")
                .httpOnly(true)
                .build();

        LoginResponse.LoginDto loginDto = new LoginResponse.LoginDto(523L, ACCESS_TOKEN_PREFIX + "accessToken", tokenCookie, LocalDateTime.now());

        //when
        given(loginService.emailLoginProcessing(any(), any(), any())).willReturn(loginDto);

        //then
        mockMvc.perform(post(EMAIL_LOGIN_URL)
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
    @DisplayName("1회용 인증 코드로 로그인")
    void oneTimeCodeLoginTest() throws Exception {
        //given
        Member member = Member.createMemberWithEmail("email@email.com", "password", "01011111234", new MemberProfile("nickname", ProfileImage.createDefaultImage("image")));

        String authenticationCode = UUID.randomUUID().toString();

        ResponseCookie tokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN_PREFIX + "RefreshToken")
                .maxAge(604800)
                .secure(true)
                .sameSite("none")
                .httpOnly(true)
                .build();

        LoginResponse.LoginDto loginDto = new LoginResponse.LoginDto(523L, ACCESS_TOKEN_PREFIX + "access-token", tokenCookie, LocalDateTime.now());

        LoginRequest.LoginByOneTimeCode code = new LoginRequest.LoginByOneTimeCode(authenticationCode);
        //when
        when(loginService.oneTimeAuthenticationCodeLogin(any(), any())).thenReturn(loginDto);
        //then
        mockMvc.perform(post(ONE_TIME_CODE_LOGIN)
                        .content(objectMapper.writeValueAsString(code))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andDo(document("login-oneTimeCode",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("code").type(STRING).description("1회용 인증 코드")
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
    @DisplayName("토큰재발급")
    void 토큰재발급() throws Exception {
        //given
        String accessToken = ACCESS_TOKEN_PREFIX + "access-token";
        Cookie refresTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE, UUID.randomUUID().toString());
        String newAccessToken = "newAccessToken";

        ResponseCookie tokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, REFRESH_TOKEN_PREFIX + "newRefreshToken")
                .maxAge(604800)
                .secure(true)
                .sameSite("none")
                .httpOnly(true)
                .build();


        LoginResponse.LoginDto loginDto = new LoginResponse.LoginDto(523L, newAccessToken, tokenCookie, LocalDateTime.now());
        //when
        when(loginService.reissueToken(any(), any())).thenReturn(loginDto);
        //then
        mockMvc.perform(post(TOKEN_REISSUE_URL)
                        .header(HttpHeaders.AUTHORIZATION, accessToken)
                        .cookie(refresTokenCookie)
                )
                .andDo(print())
                .andDo(document("login-reissue",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token")
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
                                fieldWithPath("result.links.[1].href").type(STRING).description("url")
                        )
                ));
    }
}
