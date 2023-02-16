package com.study.badrequest.api_docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;
import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.board.service.BoardCommandService;
import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.repository.CommentRepository;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.member.dto.MemberRequest;
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
class CommentApiDocs {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtLoginService loginService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    BoardCommandService boardCommandService;
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
                .profileImage(ProfileImage.builder().fullPath("기본 이미지").build())
                .nickname("nickname")
                .authority(Authority.MEMBER)
                .build();
        memberRepository.save(member);

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();
        boardCommandService.create(member.getUsername(), form, null);
    }

    @AfterEach
    void afterEach() {
        boardRepository.deleteAll();
        memberRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 추가")
    void addCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginService.loginProcessing(email, password);

        Board board = boardRepository.findByTitle("제목").get();

        CommentRequest.Create create = new CommentRequest.Create("댓글 1번 입니다.");

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
                                fieldWithPath("text").type(JsonFieldType.STRING).description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.VARIES).description("댓글 추가일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 수정")
    void putCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginService.loginProcessing(email, password);

        Member member = memberRepository.findById(loginDto.getId()).get();
        Board board = boardRepository.findByTitle("제목").get();

        CommentRequest.Create create = new CommentRequest.Create("댓글 1번 입니다.");
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
                                fieldWithPath("text").type(JsonFieldType.STRING).description("수정된 댓글")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER).description("식별 아이디"),
                                fieldWithPath("result.updatedAt").type(JsonFieldType.VARIES).description("댓글 수정일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )
                ));
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteCommentTest() throws Exception {
        //given
        String email = "tester@test.com";
        String password = "password1234!@";
        LoginResponse.LoginDto loginDto = loginService.loginProcessing(email, password);

        Member member = memberRepository.findById(loginDto.getId()).get();
        Board board = boardRepository.findByTitle("제목").get();

        CommentRequest.Create create = new CommentRequest.Create("댓글 1번 입니다.");
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
                                fieldWithPath("status").type(JsonFieldType.STRING).description("커스텀 응답상태"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("커스텀 응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("커스텀 응답 메시지"),
                                fieldWithPath("result.delete").type(JsonFieldType.VARIES).description("댓글 삭제 여부"),
                                fieldWithPath("result.deletedAt").type(JsonFieldType.VARIES).description("댓글 삭제일"),
                                fieldWithPath("result.links.[0].rel").type(JsonFieldType.STRING).description("링크 설명"),
                                fieldWithPath("result.links.[0].href").type(JsonFieldType.STRING).description("링크")
                        )));
    }
}