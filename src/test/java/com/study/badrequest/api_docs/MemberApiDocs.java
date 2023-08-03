package com.study.badrequest.api_docs;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.study.badrequest.member.command.interfaces.MemberAccountApiController;

import com.study.badrequest.member.query.interfaces.MemberQueryApiController;
import com.study.badrequest.member.command.domain.CurrentMember;
import com.study.badrequest.member.command.domain.RegistrationType;


import com.study.badrequest.member.command.interfaces.MemberRequest;
import com.study.badrequest.member.command.interfaces.MemberResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;

import com.study.badrequest.member.query.dao.MemberQueryRepository;
import com.study.badrequest.member.query.dto.LoggedInMemberInformation;
import com.study.badrequest.member.command.application.MemberService;
import com.study.badrequest.member.command.application.MemberProfileService;

import com.study.badrequest.testHelper.WithCustomMockUser;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.study.badrequest.member.command.domain.Authority.*;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.common.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;


@WebMvcTest(
        controllers = {MemberAccountApiController.class, MemberQueryApiController.class}
//        excludeFilters = {
//                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
//        }
)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class MemberApiDocs {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberProfileService memberProfileService;
    @MockBean
    private MemberQueryRepository memberQueryRepository;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private MemberResponseModelAssembler memberResponseModelAssembler;

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("인증메일 발송 요청")
    void 인증메일_발송_요청() throws Exception {
        //given
        String email = "member1@gmail.com";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiredAt = createdAt.plusMinutes(5);
        MemberRequest.SendAuthenticationEmail authenticationEmail = new MemberRequest.SendAuthenticationEmail(email);
        MemberResponse.SendAuthenticationEmail sendAuthenticationEmail = new MemberResponse.SendAuthenticationEmail(email, createdAt, expiredAt);
        //when
        given(memberService.sendAuthenticationMailProcessing(any())).willReturn(sendAuthenticationEmail);

        given(memberResponseModelAssembler.getSendAuthenticationMail(sendAuthenticationEmail)).willReturn(
                EntityModel.of(
                        sendAuthenticationEmail,
                        Link.of("https://www.bad-request.kr/api/v2/members/email-authentication")
                ));
        //then
        mockMvc.perform(post(POST_MEMBER_SEND_EMAIL_AUTHENTICATION_CODE)
                        .content(objectMapper.writeValueAsString(authenticationEmail))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))

                .andDo(print())
                .andDo(document("member_send_authentication_mail",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.email").type(STRING).description("인증된 이메일"),
                                fieldWithPath("result.startedAt").type(STRING).description("인증 시작 시간"),
                                fieldWithPath("result.expiredAt").type(STRING).description("인증 유지 시간"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("uri")
                        )
                ));

    }

    @Test
    @DisplayName("회원 가입")
    void 회원가입() throws Exception {
        //given
        String email = "member1@gmail.com";
        String password = "password1234!@";
        String nickname = "마스터오브자바";
        String contact = "01012341234";
        Long memberId = 12324L;

        //when
        MemberRequest.SignUp signUpForm = new MemberRequest.SignUp(email, password, nickname, contact, "938304");
        given(memberService.signUpWithEmail(any())).willReturn(memberId);

        //then
        mockMvc.perform(post(POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(signUpForm)))
                .andDo(print())
                .andDo(document("member-signup",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일"),
                                fieldWithPath("password").type(STRING).description("비밀번호"),
                                fieldWithPath("nickname").type(STRING).description("닉네임"),
                                fieldWithPath("contact").type(STRING).description("연락처"),
                                fieldWithPath("authenticationCode").type(STRING).description("이메일 인증 코드")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("회원 식별 아이디"),
                                fieldWithPath("result.createdAt").type(STRING).description("회원 생성 시간"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("uri"),
                                fieldWithPath("result.links.[1].href").type(STRING).description("login"),
                                fieldWithPath("result.links.[1].href").type(STRING).description("uri")
                        )
                ));
    }


    @Test
    @DisplayName("로그인된 회원 정보")
    @WithCustomMockUser(memberId = "2341", authority = MEMBER)
    void 로그인된회원정보조회() throws Exception {
        //given
        Long memberId = 2341L;
        CurrentMember currentMember = new CurrentMember(UUID.randomUUID().toString(), memberId, MEMBER.getAuthorities());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(currentMember, "", MEMBER.getAuthorities());
        LoggedInMemberInformation memberInformation = new LoggedInMemberInformation(memberId, MEMBER, "닉네임1", "", RegistrationType.GITHUB);
        //when
        SecurityContextHolder.createEmptyContext().setAuthentication(authenticationToken);
        given(memberQueryRepository.findLoggedInMemberInformation(any())).willReturn(Optional.of(memberInformation));
        //then
        mockMvc.perform(get(GET_LOGGED_IN_MEMBER_INFORMATION, memberId)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andDo(document("member-loggedInInformation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별 아이디")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("회원 식별 아이디"),
                                fieldWithPath("result.authority").type(STRING).description("회원 권한"),
                                fieldWithPath("result.nickname").type(STRING).description("닉네임"),
                                fieldWithPath("result.profileImage").type(STRING).description("프로필 이미지"),
                                fieldWithPath("result.loggedInAs").type(STRING).description("로그인 타입"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("uri")
                        ))
                );

    }
}
