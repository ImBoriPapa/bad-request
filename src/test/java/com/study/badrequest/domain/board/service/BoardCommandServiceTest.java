package com.study.badrequest.domain.board.service;


import com.study.badrequest.base.BaseMemberTest;
import com.study.badrequest.domain.member.entity.Authority;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;


import com.study.badrequest.domain.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class BoardCommandServiceTest extends BaseMemberTest {
    @Autowired
    private BoardCommandServiceImpl boardCommandService;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        String email = "tester@test.com";
        String password = "password1234!@";
        Member member = Member.createMember()
                .email(email)
                .password(passwordEncoder.encode(password))
                .contact("010-1234-1234")
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
    }

    @Test
    @DisplayName("board 생성")
    void createBoard() throws Exception {
        //given
        String email = "tester@test.com";

        Member member = memberRepository.findByEmail(email).get();

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();

        MockMultipartFile image1 = new MockMultipartFile("image", "Image.png", "image/png", "image.dsada".getBytes());
        //when
        BoardResponse.Create create = boardCommandService.create(member.getUsername(), form, List.of(image1));
        Board board = boardRepository.findById(create.getBoardId()).orElseThrow(() -> new IllegalArgumentException());
        //then
        Assertions.assertThat(board.getId()).isEqualTo(create.getBoardId());

    }

    @Test
    @DisplayName("Board 생성 이미지 없이")
    void createBoardWithImage() throws Exception {
        //given
        String email = "tester@test.com";

        Member member = memberRepository.findByEmail(email).get();

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();
        //when
        BoardResponse.Create create = boardCommandService.create(member.getUsername(), form, null);
        Board board = boardRepository.findById(create.getBoardId()).orElseThrow(() -> new IllegalArgumentException(""));
        //then
        Assertions.assertThat(board).isNotNull();
    }

    @Test
    @DisplayName("게시판 수정")
    void updateBoard() throws Exception {
        //given
        String email = "tester@test.com";
        String title = "제목";

        Member member = memberRepository.findByEmail(email).get();

        Board board = boardRepository.findByTitle(title).orElseThrow(() -> new IllegalArgumentException(""));

        BoardRequest.Update newData = BoardRequest.Update
                .builder()
                .title("변경된 제목")
                .contents("변경된 내용")
                .build();
        BoardRequest.Update form = newData;
        //when
        BoardResponse.Update update = boardCommandService.update(member.getUsername(), board.getId(), newData, null);
        Board findBoard = boardRepository.findById(update.getBoardId()).orElseThrow(() -> new IllegalArgumentException());
        //then
        Assertions.assertThat(findBoard.getTitle()).isEqualTo(newData.getTitle());
        Assertions.assertThat(findBoard.getContents()).isEqualTo(newData.getContents());
    }
}