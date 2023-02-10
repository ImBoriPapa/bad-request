package com.study.badrequest.api;

import com.study.badrequest.domain.admin.MonitorService;
import com.study.badrequest.domain.log.repositoey.query.LogQueryRepositoryImpl;
import com.study.badrequest.domain.login.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
    void getSystemTest() throws Exception {
        //given
        mockMvc.perform(get("/api/v1/dashboard/system"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andDo(print())
                .andDo(document("dashboard-system",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
        //when

        //then

    }

    // TODO: 2023/02/10 SSE 테스트 연속동작시 Not Set Content Exception
//    @Test
    @DisplayName("SSE heap data 테스트")
    void getHeapTest() throws Exception{
        //given
        mockMvc.perform(get("/api/v1/dashboard/heap"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andDo(print())
                .andDo(document("dashboard-heap",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
        //when

        //then

    }
}