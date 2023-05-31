package com.study.badrequest.utils.image;

import com.amazonaws.services.s3.AmazonS3Client;

import com.study.badrequest.exception.CustomRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static com.study.badrequest.commons.response.ApiResponseStatus.NOT_SUPPORT_ERROR;
import static com.study.badrequest.commons.response.ApiResponseStatus.WRONG_FILE_ERROR;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class S3ImageUploaderTest {

    @InjectMocks
    private S3ImageUploader imageUploader;

    @Mock
    private AmazonS3Client amazonS3Client;

    @Test
    @DisplayName("이미지 업로드 테스트")
    void imageUploadTest() throws Exception{
        //given
        MockMultipartFile file = new MockMultipartFile("image","image.png","image/png","test".getBytes());
        String folderName = "board";

        //when
        ImageUploadDto imageUploadDto = imageUploader.uploadImageFile(file, folderName);
        //then
        assertThat(imageUploadDto.getOriginalFileName()).isEqualTo(file.getOriginalFilename());
        assertThat(imageUploadDto.getStoredFileName()).isNotNull();
        assertThat(imageUploadDto.getSize()).isEqualTo(file.getSize());
    }

    @Test
    @DisplayName("확장자가 없는 파일명")
    void noExtFileName() throws Exception{
        //given
        MockMultipartFile noHasExtFile = new MockMultipartFile("image","image","image/png","test".getBytes());
        String folderName = "board";
        //when
        assertThatThrownBy(() -> imageUploader.uploadImageFile(noHasExtFile, folderName))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(WRONG_FILE_ERROR.getMessage());
        //then
    }

    @Test
    @DisplayName("지원하는 이미지 형식이 아닐때")
    void notSupportExt() throws Exception{
        //given
        MockMultipartFile noHasExtFile = new MockMultipartFile("image","image.zxc","image/png","test".getBytes());
        String folderName = "board";
        //when
        assertThatThrownBy(() -> imageUploader.uploadImageFile(noHasExtFile, folderName))
                .isInstanceOf(CustomRuntimeException.class)
                .hasMessage(NOT_SUPPORT_ERROR.getMessage());
        //then

    }

}