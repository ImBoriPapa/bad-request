package com.study.badrequest.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.domain.member.dto.MemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.consts.CustomURL.BASE_API_VERSION_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberRestDocsTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입")
    void createMember() throws Exception {
        //
        String email = "bori@gmail.com";
        String password = "password1234!@";
        String nickname = "nickname";
        String contact = "010-1234-5123";

        MemberRequest.CreateMember request = new MemberRequest.CreateMember(email,password,nickname,contact);

        //request
        mockMvc.perform(post(BASE_API_VERSION_URL + "/members")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print());


    }

}