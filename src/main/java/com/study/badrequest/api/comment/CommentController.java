package com.study.badrequest.api.comment;


import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;

import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.repository.dto.CommentDto;
import com.study.badrequest.domain.comment.repository.CommentQueryRepository;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.utils.modelAssembler.CommentResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;

@RestController
@Slf4j
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class CommentController {

    private final CommentResponseModelAssembler commentResponseModelAssembler;
    private final CommentCommendService commentCommendService;

    @CustomLogTracer
    @PostMapping("/board/{boardId}/comments")
    public ResponseEntity postComments(@AuthenticationPrincipal User user,
                                       @PathVariable Long boardId,
                                       @RequestBody CommentRequest.Create form) {

        CommentResponse.Create create = commentCommendService.addComment(boardId, user.getUsername(), form);



        return ResponseEntity
                .ok()
                .body(create);
    }
}
