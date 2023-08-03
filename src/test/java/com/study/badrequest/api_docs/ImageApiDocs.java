package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.image.command.interfaces.ImageApiController;
import com.study.badrequest.image.command.interfaces.QuestionImageResponse;
import com.study.badrequest.filter.JwtAuthenticationFilter;
import com.study.badrequest.image.command.application.QuestionImageService;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDateTime;
import java.util.UUID;

import static com.study.badrequest.common.constants.ApiURL.UPLOAD_QUESTION_IMAGE;
import static com.study.badrequest.common.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.common.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentRequest;
import static com.study.badrequest.testHelper.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = ImageApiController.class)
@Import(QuestionModelAssembler.class)
@ActiveProfiles("test")
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class ImageApiDocs {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    QuestionImageService questionImageService;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Value("${s3-image.bucket-url}")
    public String bucketUrl;

    @Test
    @DisplayName("질문 게시판 이미지 임시 저장")
    void 질문이미지임시저장() throws Exception {
        //given
        Long imageId = 214L;
        String originalFileName = "image.png";
        String imageLocation = bucketUrl + "questions/" + UUID.randomUUID() + ".png";
        String accessToken = UUID.randomUUID().toString();
        MockMultipartFile mockImage = new MockMultipartFile("image", originalFileName, "png/image", "test".getBytes());
        QuestionImageResponse.Temporary response = new QuestionImageResponse.Temporary(imageId, originalFileName, imageLocation, LocalDateTime.now());
        //when
        when(questionImageService.saveTemporaryImage(any())).thenReturn(response);
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_QUESTION_IMAGE)
                        .file(mockImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION_HEADER, ACCESS_TOKEN_PREFIX + accessToken))
                .andDo(print())
                .andDo(document("image-upload_question",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        requestParts(
                                partWithName("image").description("Image File")
                        ),
                        responseFields(
                                fieldWithPath("status").type(STRING).description("응답 상태"),
                                fieldWithPath("code").type(NUMBER).description("응답 코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("result.id").type(NUMBER).description("질문 식별 아이디"),
                                fieldWithPath("result.originalFileName").type(STRING).description("이미지 원본 파일명"),
                                fieldWithPath("result.imageLocation").type(STRING).description("이미지 경로"),
                                fieldWithPath("result.savedAt").type(STRING).description("저장 시간")
                        )
                ));

    }

}
