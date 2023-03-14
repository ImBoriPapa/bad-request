package com.study.badrequest.utils.image;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ImageUploaderTest {

    @Autowired
    ImageUploader imageUploader;

    @Test
    @DisplayName("d")
    void test() throws Exception{
        //given
        MultipartFile image = new MockMultipartFile("image", "image.png", "image/png", "test".getBytes());
        //when
        ImageDetailDto board = imageUploader.uploadFile(image, "board");
        //then
        assertThat(board.getOriginalFileName()).isEqualTo("image");
    }

}