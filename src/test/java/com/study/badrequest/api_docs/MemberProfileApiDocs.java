package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.api.member.MemberProfileApiController;
import com.study.badrequest.domain.login.CurrentMember;
import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.service.member.MemberProfileService;
import com.study.badrequest.testHelper.WithCustomMockUser;
import com.study.badrequest.utils.modelAssembler.MemberResponseModelAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.commons.constants.ApiURL.PATCH_MEMBER_NICKNAME;
import static com.study.badrequest.commons.constants.ApiURL.PATCH_MEMBER_PROFILE_IMAGE;
import static com.study.badrequest.commons.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.commons.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;
import static com.study.badrequest.domain.member.Authority.MEMBER;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = MemberProfileApiController.class)
@Import(MemberResponseModelAssembler.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class MemberProfileApiDocs {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    MemberProfileService memberProfileService;
    @Autowired
    private MemberResponseModelAssembler memberResponseModelAssembler;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("닉네임 변경 요청")
    @WithCustomMockUser(memberId = "12314", authority = MEMBER)
    void 닉네임변경() throws Exception {
        //given
        Long memberId = 12314L;
        String nickname = "변경된닉네임";
        UUID token = UUID.randomUUID();
        MemberRequest.ChangeNickname changeNickname = new MemberRequest.ChangeNickname(nickname);
        MemberResponse.Update update = new MemberResponse.Update(memberId, LocalDateTime.now());
        EntityModel<MemberResponse.Update> entityModel = EntityModel.of(update,
                Link.of("https://www.bad-request.kr/api/v2/members/" + memberId + "/nickname")
        );
        CurrentMember currentMember = new CurrentMember("sfasfasfa", memberId, MEMBER.getAuthorities());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                currentMember, "", MEMBER.getAuthorities()
        );
        SecurityContextHolder.createEmptyContext().setAuthentication(authenticationToken);
        //when
        given(memberProfileService.changeNickname(any(), any(),any())).willReturn(update);
        //then
        mockMvc.perform(patch(PATCH_MEMBER_NICKNAME, memberId)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + token)
                        .content(objectMapper.writeValueAsString(changeNickname))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andDo(document("member-changeNickname",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별 아이디")
                        ),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(STRING).description("변경할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("회원 식별 아이디"),
                                fieldWithPath("result.updatedAt").type(STRING).description("회원 생성 시간"),
                                fieldWithPath("result.links.[0].rel").type(STRING).description("self"),
                                fieldWithPath("result.links.[0].href").type(STRING).description("uri")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 이미지 변경")
    @WithCustomMockUser(memberId = "12314", authority = MEMBER)
    void 프로필이미지변경() throws Exception{
        //given
        Long memberId = 12314L;
        //when

        //then
        mockMvc.perform(patch(PATCH_MEMBER_PROFILE_IMAGE, memberId))
                .andDo(print());
    }
}
