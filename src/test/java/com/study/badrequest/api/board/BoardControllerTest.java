package com.study.badrequest.api.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.board.service.BoardCommandServiceImpl;
import com.study.badrequest.domain.login.service.LoginServiceImpl;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.entity.ProfileImage;
import com.study.badrequest.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
class BoardControllerTest extends BaseMemberTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginServiceImpl loginServiceImpl;
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    BoardCommandServiceImpl boardCommandService;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-1234")
                .profileImage(ProfileImage.createProfileImage().fullPath("?????? ?????????").build())
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);
        for (int i = 0; i <= 30; i++) {
            BoardRequest.Create form = BoardRequest.Create
                    .builder()
                    .title("??????" + i)
                    .category(Category.KNOWLEDGE)
                    .contents("??????" + i)
                    .topic(Topic.JAVA)
                    .build();
//            boardCommandService.create(member.getUsername(), form, null);
        }
    }

    @AfterEach
    void afterEach() {
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("????????? ?????? ????????? ??????")
    void createBoardTest1() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";

        LoginResponse.LoginDto loginProcessing = loginServiceImpl.login(email, password);

        BoardRequest.Create form = BoardRequest.Create.builder()
                .title("???????????????")
                .contents("??????????????? ?????? ????????? ????????? ????????? ???????????????. ?????? ????????? ????????????.")
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
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("topic").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("????????????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.boardId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.createAt").type(JsonFieldType.VARIES).description("????????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    //    @Test
    @DisplayName("????????? ?????? ????????? ??????")
    void createBoardTest2() throws Exception {
        //given
        LoginResponse.LoginDto loginProcessing = loginServiceImpl.login(SAMPLE_USER_EMAIL, SAMPLE_PASSWORD);

        BoardRequest.Create form = BoardRequest.Create.builder()
                .title("???????????????")
                .contents("??????????????? ?????? ????????? ????????? ????????? ???????????????. ?????? ????????? ????????????.")
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
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("context").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("topic").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("????????????")
                        ),
                        requestPartBody("images"),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.boardId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.createAt").type(JsonFieldType.VARIES).description("Access Token ????????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ????????? ??????")
    void getBoardTest() throws Exception {
        //given

        //when
        mockMvc.perform(get("/api/v1/board?size=3"))
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
                                fieldWithPath("result.lastIndex").type(JsonFieldType.NUMBER).description("?????? ????????? ????????? ?????????"),
                                fieldWithPath("result.results.[0].boardId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].profileImage").type(JsonFieldType.STRING).description("????????? ?????????").optional(),
                                fieldWithPath("result.results.[0].nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("result.results.[0].title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.results.[0].likeCount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("result.results.[0].category").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("result.results.[0].topic").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.results.[0].commentCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("result.results.[0].createdAt").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.results.[0].links.[0].href").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));

    }

    @Test
    @DisplayName("????????? ????????? ?????? ?????? ?????? ??????")
    void getBoardTest2() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/v1/board")
                        .param("size", "3")
                        .param("category", "KNOWLEDGE")
                        .param("topic", "JAVA")
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.size").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
                                fieldWithPath("result.lastIndex").type(JsonFieldType.NUMBER).description("?????? ????????? ????????? ?????????"),
                                fieldWithPath("result.results.[0].boardId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].profileImage").type(JsonFieldType.STRING).description("????????? ?????????").optional(),
                                fieldWithPath("result.results.[0].nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("result.results.[0].title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.results.[0].likeCount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("result.results.[0].category").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("result.results.[0].topic").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.results.[0].commentCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("result.results.[0].createdAt").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.results.[0].links.[0].href").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ?????????")
    void getBoardDetailTest() throws Exception {
        //given
        Board board = boardRepository.findByTitle("??????1").get();
        //when
        mockMvc.perform(get("/api/v1/board/{boardId}",board.getId())
                        .param("category", Category.KNOWLEDGE.name())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(CustomStatus.SUCCESS.name()))
                .andExpect(jsonPath("code").value(CustomStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("message").value(CustomStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("result.boardId").exists())
                .andExpect(jsonPath("result.memberId").exists())
                .andExpect(jsonPath("result.profileImage").exists())
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.boardId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.memberId").type(JsonFieldType.NUMBER).description("????????? ?????? ?????????"),
                                fieldWithPath("result.profileImage").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("result.nickname").type(JsonFieldType.STRING).description("????????? ?????????"),
                                fieldWithPath("result.title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.contents").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.likeCount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("result.category").type(JsonFieldType.STRING).description("????????????"),
                                fieldWithPath("result.topic").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.commentCount").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.STRING).description("????????? ?????? ??????"),
                                fieldWithPath("result.boardImages").type(JsonFieldType.ARRAY).description("????????? ?????? ?????????").optional(),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }
}