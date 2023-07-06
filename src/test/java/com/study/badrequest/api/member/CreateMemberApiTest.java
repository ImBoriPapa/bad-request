package com.study.badrequest.api.member;

import com.study.badrequest.commons.constants.ApiURL;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.dto.member.MemberRequest;
import com.study.badrequest.dto.member.MemberResponse;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;


import java.net.URI;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class CreateMemberApiTest extends MemberAccountApiTestBase {

    @Test
    @DisplayName("회원 생성 API 실패 테스트: 이메일 공백")
    void test1() throws Exception {
        //given
        final String email = "";
        final String password = "password1234!@";
        final String nickname = "닉네임";
        final String contact = "01012341234";
        final String authenticationCode = "1234";

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);
        //when

        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ApiResponseStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(ApiResponseStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());


    }

    @Test
    @DisplayName("회원 생성 API 실패 테스트: 비밀번호 공백")
    void test2() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "";
        final String nickname = "닉네임";
        final String contact = "01012341234";
        final String authenticationCode = "1234";

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);
        //when

        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ApiResponseStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(ApiResponseStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("회원 생성 API 실패 테스트: 비밀번호 형식이 안맞음")
    void test3() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "123456789";
        final String nickname = "닉네임";
        final String contact = "01012341234";
        final String authenticationCode = "1234";

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);
        //when

        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ApiResponseStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(ApiResponseStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("회원 생성 API 실패 테스트: 닉네임이 공백")
    void test4() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String nickname = "";
        final String contact = "01012341234";
        final String authenticationCode = "1234";

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);
        //when

        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ApiResponseStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(ApiResponseStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("회원 생성 API 실패 테스트: 연락처가 공백")
    void test5() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String nickname = "닉네임";
        final String contact = "";
        final String authenticationCode = "1234";

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);
        //when

        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ApiResponseStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(ApiResponseStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("회원 생성 API 실패 테스트: 인증 코드가 공백")
    void test6() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String nickname = "닉네임";
        final String contact = "01012341234";
        final String authenticationCode = "";

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);
        //when

        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(ApiResponseStatus.VALIDATION_ERROR.name()))
                .andExpect(jsonPath("code").value(ApiResponseStatus.VALIDATION_ERROR.getCode()))
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("회원 생성 API 성공 테스트")
    void test7() throws Exception {
        //given
        final String email = "email@email.com";
        final String password = "password1234!@";
        final String nickname = "닉네임";
        final String contact = "01012341234";
        final String authenticationCode = "123456";
        final Long memberId = 1L;
        final URI location = URI.create("https://bad-request/api/v2/members/" + memberId);

        MemberRequest.SignUp request = new MemberRequest.SignUp(email, password, nickname, contact, authenticationCode);

        MemberResponse.Create response = new MemberResponse.Create(memberId, LocalDateTime.now());

        //when
        when(memberService.signupMemberProcessingByEmail(any(), any())).thenReturn(response);
        when(memberResponseModelAssembler.getLocationUri(memberId)).thenReturn(location);
        //then
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

    }
}
