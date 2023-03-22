package com.study.badrequest.domain.board.service;

import com.study.badrequest.domain.board.dto.BoardImageResponse;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.repository.BoardImageRepository;
import com.study.badrequest.utils.image.ImageUplaodDto;
import com.study.badrequest.utils.image.ImageUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardImageServiceImplTest {

    @InjectMocks
    private BoardImageServiceImpl boardImageService;
    @Mock
    private ImageUploader imageUploader;
    @Mock
    private BoardImageRepository boardImageRepository;

    @Test
    @DisplayName("이미지 저장 테스트")
    void saveTest() throws Exception {
        //given
        MultipartFile image = new MockMultipartFile("image", "test1.png", "image/png", "test1".getBytes());
        String storedFileName = UUID.randomUUID().toString();

        ImageUplaodDto imageUplaodDto = ImageUplaodDto.builder()
                .originalFileName(image.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileType(image.getContentType())
                .imageLocation("https://localhost:8080/" + storedFileName)
                .size(image.getSize())
                .build();

        BoardImage boardImage = BoardImage
                .createBoardImage()
                .originalFileName(imageUplaodDto.getOriginalFileName())
                .storedFileName(imageUplaodDto.getStoredFileName())
                .imageLocation(imageUplaodDto.getImageLocation())
                .fileType(imageUplaodDto.getFileType())
                .size(imageUplaodDto.getSize())
                .build();
        //when
        when(imageUploader.uploadFile((MultipartFile) any(), any())).thenReturn(imageUplaodDto);
        when(boardImageRepository.save(any())).thenReturn(boardImage);
        BoardImageResponse.Create response = boardImageService.save(image);
        //then
        assertThat(response.getImageLocation()).isNotNull();
    }

}