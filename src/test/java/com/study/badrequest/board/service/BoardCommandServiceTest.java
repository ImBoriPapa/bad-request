package com.study.badrequest.board.service;


import com.study.badrequest.domain.login.dto.LoginResponse;
import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.domain.member.entity.Member;
import com.study.badrequest.domain.member.repository.MemberRepository;
import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;


import com.study.badrequest.domain.board.repository.BoardRepository;
import com.study.badrequest.domain.board.service.BoardCommandService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.badrequest.SampleUserData.SAMPLE_USER_EMAIL;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class BoardCommandServiceTest {
    @Autowired
    private BoardCommandService boardCommandService;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtLoginService loginService;

    @Test
    @DisplayName("board 생성")
    void createBoard() throws Exception {
        //given
        Member member = memberRepository.findByEmail(SAMPLE_USER_EMAIL).get();

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();

        MockMultipartFile image1 = new MockMultipartFile("image", "Image.png", "image/png", "image.dsada".getBytes());
        //when
        BoardResponse.Create create = boardCommandService.create(member.getUsername(),form, List.of(image1));
        Board board = boardRepository.findById(create.getBoardId()).orElseThrow(() -> new IllegalArgumentException());
        //then
        Assertions.assertThat(board.getId()).isEqualTo(create.getBoardId());

    }

    @Test
    @DisplayName("Board 생성 이미지 없이")
    void createBoardWithImage() throws Exception {
        //given
        Member member = memberRepository.findByEmail(SAMPLE_USER_EMAIL).get();

        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .title("제목")
                .category(Category.KNOWLEDGE)
                .contents("내용")
                .topic(Topic.JAVA)
                .build();
        //when
        BoardResponse.Create create = boardCommandService.create(member.getUsername(),form, null);
        Board board = boardRepository.findById(create.getBoardId()).orElseThrow(() -> new IllegalArgumentException(""));
        //then
        Assertions.assertThat(board).isNotNull();
    }

    @Test
    @DisplayName("게시판 수정")
    void updateBoard() throws Exception{
        //given
        Member member = memberRepository.findByEmail(SAMPLE_USER_EMAIL).get();
        Board board = boardRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException(""));
        BoardRequest.Update newData = BoardRequest.Update
                .builder()
                .title("변경된 제목")
                .contents("변경된 내용")
                .build();
        BoardRequest.Update form = newData;
        //when
        BoardResponse.Update update = boardCommandService.update(member.getUsername(),board.getId(), newData, null);
        Board findBoard = boardRepository.findById(update.getBoardId()).orElseThrow(() -> new IllegalArgumentException());
        //then
        Assertions.assertThat(findBoard.getTitle()).isEqualTo(newData.getTitle());
        Assertions.assertThat(findBoard.getContents()).isEqualTo(newData.getContents());
    }
}