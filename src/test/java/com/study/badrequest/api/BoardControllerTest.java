package com.study.badrequest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.login.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.SampleUserData.SAMPLE_PASSWORD;
import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;
import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Slf4j
@Transactional
@ActiveProfiles("test")
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtLoginService jwtLoginService;
    @Autowired
    BoardRepository boardRepository;

    @Test
    @DisplayName("게시판 작성 이미지 없이")
    void createBoardTest1() throws Exception {
        //given
        LoginResponse.LoginDto loginProcessing = jwtLoginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);

        BoardRequest.Create form = BoardRequest.Create.builder()
                .memberId(loginProcessing.getId())
                .title("제목입니다")
                .context("내용입니다 무슨 내용을 넣을까 고민이 되었습니다. 좋은 내용을 찾습니다.")
                .topic(Topic.JAVA)
                .category(Category.KNOWLEDGE)
                .build();
        String value = objectMapper.writeValueAsString(form);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("form", "form", MediaType.APPLICATION_JSON_VALUE, value.getBytes());
        //when
        mockMvc.perform(multipart("/api/v1/board")
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginProcessing.getAccessToken())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.boardId").exists())
                .andExpect(jsonPath("result.createAt").exists())
                .andExpect(jsonPath("result.links").exists())
                .andExpect(jsonPath("result.links.[0].href").exists())
                .andExpect(jsonPath("result.links.[0].rel").exists())
                .andDo(print())
                .andDo(document("board_create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),

                        requestPartFields("form",
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("context").type(JsonFieldType.STRING).description("본문"),
                                fieldWithPath("topic").type(JsonFieldType.STRING).description("주제"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.boardId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.createAt").type(JsonFieldType.VARIES).description("게시판 생성일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));
    }

    @Test
    @DisplayName("게시판 작성 이미지 첨부")
    void createBoardTest2() throws Exception {
        //given
        LoginResponse.LoginDto loginProcessing = jwtLoginService.loginProcessing(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);

        BoardRequest.Create form = BoardRequest.Create.builder()
                .memberId(loginProcessing.getId())
                .title("제목입니다")
                .context("내용입니다 무슨 내용을 넣을까 고민이 되었습니다. 좋은 내용을 찾습니다.")
                .topic(Topic.JAVA)
                .category(Category.KNOWLEDGE)
                .build();
        String value = objectMapper.writeValueAsString(form);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("form", "form", MediaType.APPLICATION_JSON_VALUE, value.getBytes());
        MockMultipartFile image1 = new MockMultipartFile("images", "image1.png", "image/png", "image1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "image2.png", "image/png", "image2".getBytes());
        //when
        mockMvc.perform(multipart("/api/v1/board")
                        .file(mockMultipartFile)
                        .file(image1)
                        .file(image2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginProcessing.getAccessToken())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.boardId").exists())
                .andExpect(jsonPath("result.createAt").exists())
                .andExpect(jsonPath("result.links").exists())
                .andExpect(jsonPath("result.links.[0].href").exists())
                .andExpect(jsonPath("result.links.[0].rel").exists())
                .andDo(print())
                .andDo(document("board_create_add_image",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),

                        requestPartFields("form",
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("context").type(JsonFieldType.STRING).description("본문"),
                                fieldWithPath("topic").type(JsonFieldType.STRING).description("주제"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리")
                        ),
                        requestPartBody("images"),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.boardId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.createAt").type(JsonFieldType.VARIES).description("Access Token 만료기한"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));
    }

    @Test
    @DisplayName("게시판 리스트 조회")
    void getBoardTest() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/v1/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.size").exists())
                .andExpect(jsonPath("result.hasNext").exists())
                .andExpect(jsonPath("result.lastIndex").exists())
                .andExpect(jsonPath("result.results").exists())
                .andExpect(jsonPath("result.results.[0].boardId").exists())
                .andExpect(jsonPath("result.results.[0].memberId").exists())
                .andExpect(jsonPath("result.results.[0].profileImage").exists())
                .andExpect(jsonPath("result.results.[0].nickname").exists())
                .andExpect(jsonPath("result.results.[0].title").exists())
                .andExpect(jsonPath("result.results.[0].likeCount").exists())
                .andExpect(jsonPath("result.results.[0].category").exists())
                .andExpect(jsonPath("result.results.[0].topic").exists())
                .andExpect(jsonPath("result.results.[0].commentCount").exists())
                .andExpect(jsonPath("result.results.[0].createdAt").exists())
                .andExpect(jsonPath("result.results.[0].links").exists())
                .andExpect(jsonPath("result.results.[0].links.[0].rel").exists())
                .andExpect(jsonPath("result.results.[0].links.[0].href").exists())
                .andExpect(jsonPath("result.links").exists())
                .andExpect(jsonPath("result.links.[0].href").exists())
                .andExpect(jsonPath("result.links.[0].rel").exists())
                .andDo(print())

                //then
                .andDo(document("board_getList_no_condition",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("리스트 사이즈"),
                                fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 데이터 유무"),
                                fieldWithPath("result.lastIndex").type(JsonFieldType.NUMBER).description("요청 데이터 마지막 인덱스"),
                                fieldWithPath("result.results.[0].boardId").type(JsonFieldType.NUMBER).description("게시판 아이디"),
                                fieldWithPath("result.results.[0].memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("result.results.[0].profileImage").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("result.results.[0].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("result.results.[0].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("result.results.[0].likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("result.results.[0].category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("result.results.[0].topic").type(JsonFieldType.STRING).description("주제"),
                                fieldWithPath("result.results.[0].commentCount").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("result.results.[0].createdAt").type(JsonFieldType.STRING).description("게시판 생성일"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.results.[0].links.[0].href").type(JsonFieldType.STRING).description("링크"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));

    }

    @Test
    @DisplayName("게시판 리스트 조회 검색 조건 추가")
    void getBoardTest2() throws Exception {
        //given

        //when
        mockMvc.perform(get("/api/v1/board")
                        .param("size", "5")
                        .param("lastIndex", "37")
                        .param("category", "question")
                        .param("topic", "mysql")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.size").exists())
                .andExpect(jsonPath("result.hasNext").exists())
                .andExpect(jsonPath("result.lastIndex").exists())
                .andExpect(jsonPath("result.results").exists())
                .andExpect(jsonPath("result.results.[0].boardId").exists())
                .andExpect(jsonPath("result.results.[0].memberId").exists())
                .andExpect(jsonPath("result.results.[0].profileImage").exists())
                .andExpect(jsonPath("result.results.[0].nickname").exists())
                .andExpect(jsonPath("result.results.[0].title").exists())
                .andExpect(jsonPath("result.results.[0].likeCount").exists())
                .andExpect(jsonPath("result.results.[0].category").exists())
                .andExpect(jsonPath("result.results.[0].topic").exists())
                .andExpect(jsonPath("result.results.[0].commentCount").exists())
                .andExpect(jsonPath("result.results.[0].createdAt").exists())
                .andExpect(jsonPath("result.results.[0].links").exists())
                .andExpect(jsonPath("result.results.[0].links.[0].rel").exists())
                .andExpect(jsonPath("result.results.[0].links.[0].href").exists())
                .andExpect(jsonPath("result.links").exists())
                .andExpect(jsonPath("result.links.[0].href").exists())
                .andExpect(jsonPath("result.links.[0].rel").exists())
                .andDo(print())

                //then
                .andDo(document("board_getList_with_condition",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("리스트 사이즈"),
                                fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("다음 데이터 유무"),
                                fieldWithPath("result.lastIndex").type(JsonFieldType.NUMBER).description("요청 데이터 마지막 인덱스"),
                                fieldWithPath("result.results.[0].boardId").type(JsonFieldType.NUMBER).description("게시판 아이디"),
                                fieldWithPath("result.results.[0].memberId").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("result.results.[0].profileImage").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                                fieldWithPath("result.results.[0].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("result.results.[0].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("result.results.[0].likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("result.results.[0].category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("result.results.[0].topic").type(JsonFieldType.STRING).description("주제"),
                                fieldWithPath("result.results.[0].commentCount").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("result.results.[0].createdAt").type(JsonFieldType.STRING).description("게시판 생성일"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.results.[0].links.[0].href").type(JsonFieldType.STRING).description("링크"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));

    }

    @Test
    @DisplayName("게시판 디테일")
    void getBoardDetailTest() throws Exception {
        //given
        Board board = boardRepository.findById(1L).get();
        //when
        mockMvc.perform(get("/api/v1/board/1")
                        .param("category", board.getCategory().name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.boardId").exists())
                .andExpect(jsonPath("result.memberId").exists())
                .andExpect(jsonPath("result.nickname").exists())
                .andExpect(jsonPath("result.title").exists())
                .andExpect(jsonPath("result.contents").exists())
                .andExpect(jsonPath("result.likeCount").exists())
                .andExpect(jsonPath("result.category").exists())
                .andExpect(jsonPath("result.topic").exists())
                .andExpect(jsonPath("result.commentCount").exists())
                .andExpect(jsonPath("result.createdAt").exists())
                .andExpect(jsonPath("result.updatedAt").exists())
                .andExpect(jsonPath("result.boardImages").exists())
                .andExpect(jsonPath("result.links.[0].rel").exists())
                .andExpect(jsonPath("result.links.[0].href").exists())
                .andDo(print())
                //then
                .andDo(document("board_get_detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.boardId").type(JsonFieldType.NUMBER).description("게시판 아이디"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("작성자 회원 아이디"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("작성자 닉네임"),
                                fieldWithPath("result.title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("result.contents").type(JsonFieldType.STRING).description("본문"),
                                fieldWithPath("result.likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("result.topic").type(JsonFieldType.STRING).description("주제"),
                                fieldWithPath("result.commentCount").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("게시판 생성 일자"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("게시판 수정 일자"),
                                fieldWithPath("result.boardImages").type(JsonFieldType.ARRAY).description("게시판 첨부 이미지").optional(),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));
    }
}