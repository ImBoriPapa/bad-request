package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.service.LoginServiceImpl;
import com.study.badrequest.domain.member.dto.MemberRequest;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.member.service.MemberCommandServiceImpl;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Slf4j
@Transactional
@ActiveProfiles("test")
public class MemberApiDocs extends BaseMemberTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginServiceImpl loginServiceImpl;
    @Autowired
    MemberCommandServiceImpl memberCommandService;
    @Autowired
    MemberRepository memberRepository;

    private final String sampleEmail = "sample@google.com";
    private final String sampleNickname = "sample";
    private final String sampleContact = "010-1234-1234";
    private final String samplePassword = "sample1234!@";

    @BeforeEach
    void beforeEach() {
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email(sampleEmail)
                .nickname(sampleNickname)
                .password(samplePassword)
                .contact(sampleContact)
                .build();
        memberCommandService.signupMember(form);
    }

    @Test
    @DisplayName("????????????")
    void ????????????() throws Exception {
        //given
        MemberRequest.CreateMember form = MemberRequest.CreateMember.builder()
                .email("tester@test.com")
                .nickname("tester")
                .password("test1234!@")
                .contact("010-1111-1234")
                .build();
        //when
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.result.memberId").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.links").exists())
                .andExpect(jsonPath("$.result.links.[0].rel").exists())
                .andExpect(jsonPath("$.result.links.[0].href").exists())
                .andDo(print())
                .andDo(document("postMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("contact").type(JsonFieldType.STRING).description("?????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"))));
    }


    @Test
    @DisplayName("???????????? ?????? ?????????")
    void patchPasswordTest() throws Exception {
        //given
        String newPassword = "newPassword1234!@";

        MemberRequest.ResetPassword resetForm = new MemberRequest.ResetPassword(samplePassword, newPassword);
        String content = objectMapper.writeValueAsString(resetForm);

        LoginResponse.LoginDto loginResult = loginServiceImpl.login(sampleEmail, samplePassword);

        //when
        mockMvc.perform(patch("/api/v1/members/{memberId}/password", loginResult.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginResult.getAccessToken()))

                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("putPassword",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("?????? ?????? ?????????").attributes()
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("?????? ????????????"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("????????? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"))));
    }

    @Test
    @DisplayName("????????? ?????? ?????????")
    void putContactTest() throws Exception {
        //given
        String newContact = "010-1212-1312";
        MemberRequest.UpdateContact contact = new MemberRequest.UpdateContact(newContact);
        String content = objectMapper.writeValueAsString(contact);

        LoginResponse.LoginDto loginResult = loginServiceImpl.login(sampleEmail, samplePassword);

        //when
        mockMvc.perform(patch("/api/v1/members/{memberId}/contact", loginResult.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginResult.getAccessToken()))

                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("putContact",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("?????? ?????? ?????????").attributes()
                        ),
                        requestFields(
                                fieldWithPath("contact").type(JsonFieldType.STRING).description("??? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"))));
    }

    @Test
    @DisplayName("?????? ?????? ?????????")
    void deleteMemberTest() throws Exception {
        //given
        MemberRequest.DeleteMember password = new MemberRequest.DeleteMember(samplePassword);
        String content = objectMapper.writeValueAsString(password);

        LoginResponse.LoginDto loginResult = loginServiceImpl.login(sampleEmail, samplePassword);

        //when
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/members/{memberId}", loginResult.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginResult.getAccessToken()))

                //then
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("deleteMember",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        pathParameters(
                                parameterWithName("memberId").description("?????? ?????? ?????????").attributes()
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.thanks.thanks").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"))));
    }

    @Test
    @DisplayName("????????? ????????????")
    void duplicateEmailTest() throws Exception {
        //given
        String email = "email@email.com";

        mockMvc.perform(get("/api/v1/members/email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andDo(document("email_duplicate_success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.duplicate").type(JsonFieldType.BOOLEAN).description("?????? ??????"),
                                fieldWithPath("result.email").type(JsonFieldType.STRING).description("?????? ?????????")
                        )
                ));
        //when
        String duple = sampleEmail;
        mockMvc.perform(get("/api/v1/members/email")
                        .param("email", duple)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andDo(document("email_duplicate_fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("requestPath").type(JsonFieldType.STRING).description("?????? URI"),
                                fieldWithPath("errorCode").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.ARRAY).description("?????? ?????????")
                        )
                ));


    }

    @Test
    @DisplayName("Member Info ?????????")
    void memberInfoTest() throws Exception {
        //given
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(sampleEmail, samplePassword);

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/members/auth")
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("?????? ?????? ?????????"),
                                fieldWithPath("result.authority").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links").type(JsonFieldType.ARRAY).description("?????? ??????")
//                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
//                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )));
    }
}
