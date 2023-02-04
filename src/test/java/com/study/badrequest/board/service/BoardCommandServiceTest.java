package com.study.badrequest.board.service;


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
import java.util.Optional;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
class BoardCommandServiceTest {
    @Autowired
    private BoardCommandService boardCommandService;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("board 생성")
    void createBoard() throws Exception {
        //given
        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .memberId(1L)
                .title("제목")
                .category(Category.KNOWLEDGE)
                .context("내용")
                .topic(Topic.JAVA)
                .build();

        MockMultipartFile image1 = new MockMultipartFile("image", "Image.png", "image/png", "image.dsada".getBytes());
        //when
        BoardResponse.Create create = boardCommandService.create(form, List.of(image1));

        //then

    }

    @Test
    @DisplayName("Board 생성 이미지 없이")
    void createBoardWithImage() throws Exception {
        //given
        BoardRequest.Create form = BoardRequest.Create
                .builder()
                .memberId(1L)
                .title("제목")
                .category(Category.KNOWLEDGE)
                .context("내용")
                .topic(Topic.JAVA)
                .build();

        //when
        BoardResponse.Create create = boardCommandService.create(form, null);
        Board board = boardRepository.findById(create.getBoardId()).orElseThrow(()->new IllegalArgumentException(""));
        //then
        Assertions.assertThat(board).isNotNull();
    }
}