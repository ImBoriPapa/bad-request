package com.study.badrequest.api.member;

import com.study.badrequest.api_docs.MemberApiDocs;
import com.study.badrequest.commons.constants.ApiURL;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Slf4j
@WebMvcTest(controllers = MemberApiDocs.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class 회원생성 {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("test")
    void test() throws Exception {
        //given
        mockMvc.perform(post(ApiURL.POST_MEMBER_URL))
                .andDo(print());
        //when

        //then

    }
}
