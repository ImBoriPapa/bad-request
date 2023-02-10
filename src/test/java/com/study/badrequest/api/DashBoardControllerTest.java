package com.study.badrequest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class DashBoardControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("SSE system data 테스트")
    void getSystem() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/v1/dashboard/system"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type",MediaType.TEXT_EVENT_STREAM_VALUE))
                .andDo(print());

        //then

    }
}