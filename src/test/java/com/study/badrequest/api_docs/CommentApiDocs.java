package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.board.service.BoardCommandServiceImpl;
import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.service.LoginServiceImpl;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Slf4j
@Transactional
@ActiveProfiles("test")
class CommentApiDocs extends BaseMemberTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginServiceImpl loginServiceImpl;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardCommandServiceImpl boardCommandService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentCommendService commentCommendService;

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

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("??????")
                .category(Category.KNOWLEDGE)
                .contents("??????")
                .topic(Topic.JAVA)
                .build();
//        boardCommandService.create(member.getUsername(), form, null);
    }

    @AfterEach
    void afterEach() {
        boardRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("?????? ??????")
    void addCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");

        //when
        mockMvc.perform(post("/api/v1/board/{boardId}/comments", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                        .content(objectMapper.writeValueAsString(create))
                )
                //then
                .andDo(print())
                .andDo(document("comment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("text").type(JsonFieldType.STRING).description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.VARIES).description("?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ??????")
    void putCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Member member = memberRepository.findById(loginDto.getId()).get();
        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");
        CommentResponse.Create comment = commentCommendService.addComment(board.getId(), member.getUsername(), create);

        //when
        mockMvc.perform(put("/api/v1/board/{boardId}/comments/{commentId}", board.getId(), comment.getCommentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                        .content(objectMapper.writeValueAsString(create))
                )
                //then
                .andDo(print())
                .andDo(document("comment-put",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("text").type(JsonFieldType.STRING).description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.VARIES).description("?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("?????? ??????")
    void deleteCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Member member = memberRepository.findById(loginDto.getId()).get();
        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");
        CommentResponse.Create comment = commentCommendService.addComment(board.getId(), member.getUsername(), create);

        //when
        mockMvc.perform(delete("/api/v1/board/{boardId}/comments/{commentId}", board.getId(), comment.getCommentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                )
                //then
                .andDo(print())
                .andDo(document("comment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.delete").type(JsonFieldType.VARIES).description("?????? ?????? ??????"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.VARIES).description("?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??????")
    void getComments() throws Exception {
        //given
        String email = "tester@test.com";
        Member member = memberRepository.findByEmail(email).get();
        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create1 = new CommentRequest.Create("?????? 1??? ?????????.");
        CommentRequest.Create create2 = new CommentRequest.Create("?????? 2??? ?????????.");
        CommentRequest.Create create3 = new CommentRequest.Create("?????? 3??? ?????????.");
        CommentRequest.Create create4 = new CommentRequest.Create("?????? 4??? ?????????.");
        CommentRequest.Create create5 = new CommentRequest.Create("?????? 5??? ?????????.");
        CommentRequest.Create create6 = new CommentRequest.Create("?????? 6??? ?????????.");

        commentCommendService.addComment(board.getId(), member.getUsername(), create1);
        commentCommendService.addComment(board.getId(), member.getUsername(), create2);
        commentCommendService.addComment(board.getId(), member.getUsername(), create3);
        commentCommendService.addComment(board.getId(), member.getUsername(), create4);
        commentCommendService.addComment(board.getId(), member.getUsername(), create5);
        commentCommendService.addComment(board.getId(), member.getUsername(), create6);
        //when
        mockMvc.perform(get("/api/v1/board/{boardId}/comments", board.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )

                //then
                .andDo(print())
                .andDo(document("comment-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.commentSize").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("?????? ?????? ?????? ??????"),
                                fieldWithPath("result.lastIndex").type(JsonFieldType.NUMBER).description("????????? ?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.results.[0].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].boardId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].profileImage").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                fieldWithPath("result.results.[0].nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("result.results.[0].text").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.results.[0].likeCount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("result.results.[0].subCommentCount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("result.results.[0].createdAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].updatedAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.results.[0].links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )));
    }

    @Test
    @DisplayName("????????? ??????")
    void addSubCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");

        Member member = memberRepository.findById(loginDto.getId()).get();

        CommentResponse.Create comment = commentCommendService.addComment(board.getId(), member.getUsername(), create);

        CommentRequest.Create creatSub = new CommentRequest.Create("????????? ?????????.");
        //when
        mockMvc.perform(post("/api/v1/comments/{commentId}/sub-comments", comment.getCommentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                        .content(objectMapper.writeValueAsString(creatSub))
                )
                //then
                .andDo(print())
                .andDo(document("subComment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("text").type(JsonFieldType.STRING).description("?????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.subCommentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.VARIES).description("????????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ??????")
    void putSubCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");

        Member member = memberRepository.findById(loginDto.getId()).get();

        CommentResponse.Create comment = commentCommendService.addComment(board.getId(), member.getUsername(), create);

        CommentRequest.Create creatSub = new CommentRequest.Create("????????? ?????????.");

        CommentResponse.CreateSub createSub = commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub);

        //when
        mockMvc.perform(put("/api/v1/comments/{commentId}/sub-comments/{subCommentId}", comment.getCommentId(), createSub.getSubCommentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                        .content(objectMapper.writeValueAsString(creatSub))
                )
                .andDo(print())
                .andDo(document("subComment-put",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("text").type(JsonFieldType.STRING).description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.subCommentId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.VARIES).description("????????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ??????")
    void deleteSubCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");

        Member member = memberRepository.findById(loginDto.getId()).get();

        CommentResponse.Create comment = commentCommendService.addComment(board.getId(), member.getUsername(), create);

        CommentRequest.Create creatSub = new CommentRequest.Create("????????? ?????????.");

        CommentResponse.CreateSub createSub = commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub);

        //when
        mockMvc.perform(delete("/api/v1/comments/{commentId}/sub-comments/{subCommentId}", comment.getCommentId(), createSub.getSubCommentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, "Bearer " + loginDto.getAccessToken())
                        .content(objectMapper.writeValueAsString(creatSub))
                )
                .andDo(print())
                .andDo(document("subComment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(AUTHORIZATION_HEADER).description("AccessToken")
                        ),
                        requestFields(
                                fieldWithPath("text").type(JsonFieldType.STRING).description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.delete").type(JsonFieldType.BOOLEAN).description("????????? ?????? ??????"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.VARIES).description("????????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )
                ));
    }

    @Test
    @DisplayName("????????? ??????")
    void getSubCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginServiceImpl.login(email, password);

        Board board = boardRepository.findByTitle("??????").get();

        CommentRequest.Create create = new CommentRequest.Create("?????? 1??? ?????????.");

        Member member = memberRepository.findById(loginDto.getId()).get();

        CommentResponse.Create comment = commentCommendService.addComment(board.getId(), member.getUsername(), create);

        CommentRequest.Create creatSub1 = new CommentRequest.Create("A ????????? ?????????.");
        CommentRequest.Create creatSub2 = new CommentRequest.Create("B ????????? ?????????.");
        CommentRequest.Create creatSub3 = new CommentRequest.Create("C ????????? ?????????.");
        CommentRequest.Create creatSub4 = new CommentRequest.Create("D ????????? ?????????.");
        CommentRequest.Create creatSub5 = new CommentRequest.Create("E ????????? ?????????.");

        commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub1);
        commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub2);
        commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub3);
        commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub4);
        commentCommendService.addSubComment(comment.getCommentId(), member.getUsername(), creatSub5);

        //when
        mockMvc.perform(get("/api/v1/comments/{commentId}/sub-comments?size=3", comment.getCommentId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("subComment-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("????????? ????????????"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("????????? ?????? ??????"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????? ?????????"),
                                fieldWithPath("result.subCommentSize").type(JsonFieldType.NUMBER).description("?????? ??????"),
                                fieldWithPath("result.hasNext").type(JsonFieldType.BOOLEAN).description("?????? ?????? ?????? ??????"),
                                fieldWithPath("result.lastIndex").type(JsonFieldType.NUMBER).description("????????? ?????? ?????????"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("result.results.[0].subCommentId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].commentId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].boardId").type(JsonFieldType.NUMBER).description("????????? ?????????"),
                                fieldWithPath("result.results.[0].memberId").type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].profileImage").type(JsonFieldType.STRING).description("????????? ????????? ??????"),
                                fieldWithPath("result.results.[0].nickname").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("result.results.[0].text").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.results.[0].likeCount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("result.results.[0].createdAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].updatedAt").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("result.results.[0].links.[0].rel").type(JsonFieldType.STRING).description("?????? ??????"),
                                fieldWithPath("result.results.[0].links.[0].href").type(JsonFieldType.STRING).description("??????")
                        )));
    }
}