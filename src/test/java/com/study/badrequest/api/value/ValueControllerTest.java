package com.study.badrequest.api.value;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.study.badrequest.api.value.ValueController.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
class ValueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("커스텀 스테이스 조회")
    void getCustomStatus() throws Exception {
        mockMvc.perform(get(VALUES + STATUS))
                .andDo(print())
                .andDo(document("custom-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[0].status").type(JsonFieldType.STRING).description("커스텀 스테이터스 이름"),
                                fieldWithPath("[0].code").type(JsonFieldType.NUMBER).description("커스텀 스테이터스 코드"),
                                fieldWithPath("[0].message").type(JsonFieldType.STRING).description("커스텀 스테이터스 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("Category 목록 조회")
    void getCategoryTest() throws Exception {
        mockMvc.perform(get(VALUES + CATEGORY))
                .andDo(print())
                .andDo(document("category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[0].category").type(JsonFieldType.STRING).description("카테고리명"),
                                fieldWithPath("[0].explain").type(JsonFieldType.STRING).description("카테고리 설명")
                        )
                ));
    }

    @Test
    @DisplayName("Topic 목록 조회")
    void getTopicTest() throws Exception {
        mockMvc.perform(get(VALUES + TOPIC))
                .andDo(print())
                .andDo(document("topic",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[0].topic").type(JsonFieldType.STRING).description("토픽명"),
                                fieldWithPath("[0].explain").type(JsonFieldType.STRING).description("토픽 설명")
                        )
                ));
    }
}