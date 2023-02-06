package com.study.badrequest.api;


import com.study.badrequest.aop.annotation.CustomLogger;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;

import com.study.badrequest.domain.comment.repository.dto.CommentDto;
import com.study.badrequest.domain.comment.repository.CommentQueryRepository;
import com.study.badrequest.utils.model.CommentResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;

@RestController
@Slf4j
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class CommentController {
    private final CommentQueryRepository queryRepository;
    private final CommentResponseModelAssembler commentResponseModelAssembler;

    @CustomLogger
    @GetMapping("/board/{boardId}/comments")
    public ResponseEntity getComments(@PathVariable Long boardId) {

        List<CommentDto> comments = queryRepository.findAllCommentAndSubCommentByBoardId(boardId);

        CollectionModel collectionModel = commentResponseModelAssembler.toCollectionModel(comments);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, collectionModel));
    }
}
