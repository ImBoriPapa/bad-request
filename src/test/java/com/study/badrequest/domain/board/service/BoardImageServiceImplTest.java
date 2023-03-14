package com.study.badrequest.domain.board.service;

import com.study.badrequest.domain.board.dto.BoardImageResponse;
import com.study.badrequest.domain.board.entity.BoardImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BoardImageServiceImplTest {

    @InjectMocks
    private BoardImageServiceImpl boardImageService;

    @Test
    @DisplayName("이미지 저장 테스트")
    void saveTest() throws Exception{
        //given
        MultipartFile image1 = new MockMultipartFile("image","test1.png","image/png","test1".getBytes());
        //when
        BoardImageResponse.Create response = boardImageService.save(image1);
        //then

    }

}