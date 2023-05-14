package com.study.badrequest.api.image;


import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.dto.image.QuestionImageResponse;
import com.study.badrequest.service.image.QuestionImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionImageApiController {

    private final QuestionImageService questionImageService;

    @PostMapping("/api/v2/image/question")
    public ResponseEntity uploadTemporaryImage(@RequestPart(name = "image", required = false) MultipartFile image) {

        if (image == null) {
            throw new IllegalArgumentException("업로드할 이미지 파일이 찾을 수 없습니다.");
        }

        log.info("질문 게시글 이미지 임시저장 요청");
        QuestionImageResponse.Temporary temporary = questionImageService.saveTemporaryImage(image);

        return ResponseEntity.ok()
                .body(new ApiResponse.Success<>(SUCCESS, temporary));
    }

}
