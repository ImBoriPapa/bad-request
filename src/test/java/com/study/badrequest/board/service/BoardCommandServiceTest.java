package com.study.badrequest.board.service;



import com.study.badrequest.domain.board.dto.BoardRequest;
import com.study.badrequest.domain.board.dto.BoardResponse;
import com.study.badrequest.domain.board.entity.Board;
import com.study.badrequest.domain.board.entity.Category;
import com.study.badrequest.domain.board.entity.Topic;


import com.study.badrequest.domain.board.service.BoardCommandService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@Slf4j
class BoardCommandServiceTest {

    @Autowired
    private BoardCommandService boardCommandService;


    // TODO: 2023/01/31 Test Server 에서 테스트 깨지는 이유 확인
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
        log.error("ERROR");
        //when
        BoardResponse.Create create = boardCommandService.create(form, List.of(image1));
        log.info("Board");
        //then

    }
}