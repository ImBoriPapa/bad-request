package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.comment.repository.dto.CommentDto;
import com.study.badrequest.domain.comment.repository.CommentQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {
    private final CommentQueryRepository queryRepository;

    @CustomLogger
    @GetMapping("/api/v1/board/{boardId}/comments")
    public ResponseEntity getComments(@PathVariable Long boardId) {

        List<CommentDto> comments = queryRepository.findCommentByBoardUseTwoQuery(boardId);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, comments));
    }
}
