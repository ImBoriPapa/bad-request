package com.study.badrequest.image.command.interfaces;


import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.dto.image.QuestionImageResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.image.command.application.QuestionImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import static com.study.badrequest.common.constants.ApiURL.UPLOAD_QUESTION_IMAGE;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageApiController {

    private final QuestionImageService questionImageService;

    @PostMapping(value = UPLOAD_QUESTION_IMAGE,consumes = MULTIPART_FORM_DATA_VALUE,produces = APPLICATION_JSON_VALUE)
    public ResponseEntity uploadTemporaryImage(@RequestPart(name = "image", required = false) MultipartFile image) {

        if (image == null) {
            log.error("질문 게시글 이미지 파일이 없습니다.");
            throw CustomRuntimeException.createWithApiResponseStatus(NOT_FOUND_IMAGE_FILE);
        }

        log.info("질문 게시글 이미지 임시저장 요청 이미지 파일명: {}", image.getOriginalFilename());

        QuestionImageResponse.Temporary response = questionImageService.saveTemporaryImage(image);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, response));
    }

}
