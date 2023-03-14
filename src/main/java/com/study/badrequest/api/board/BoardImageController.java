package com.study.badrequest.api.board;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.board.dto.BoardImageResponse;
import com.study.badrequest.domain.board.entity.BoardImage;
import com.study.badrequest.domain.board.service.BoardImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class BoardImageController {

    private final BoardImageService boardImageService;

    @PostMapping("/api/v1/image/board")
    public ResponseEntity create(@RequestPart(name = "image") MultipartFile image) {

        BoardImageResponse.Create response = boardImageService.save(image);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, response));
    }
}
