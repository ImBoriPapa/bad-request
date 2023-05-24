package com.study.badrequest.api.comment;

import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.repository.comment.query.CommentListDto;
import com.study.badrequest.repository.comment.CommentQueryRepository;

import com.study.badrequest.repository.comment.CommentSearchCondition;
import com.study.badrequest.repository.comment.query.SubCommentListDto;
import com.study.badrequest.utils.modelAssembler.CommentResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.study.badrequest.commons.constants.ApiURL.BASE_API_VERSION_URL;


@RestController
@Slf4j
@RequestMapping(BASE_API_VERSION_URL)
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryRepository queryRepository;
    private final CommentResponseModelAssembler commentResponseModelAssembler;

    @CustomLogTracer
    @GetMapping("/board/{boardId}/comments")
    public ResponseEntity getComments(@PathVariable Long boardId, CommentSearchCondition condition) {

        CommentListDto commentListDto = queryRepository.findAllCommentByBoardId(boardId, condition);

        EntityModel<CommentListDto> entityModel = commentResponseModelAssembler.toListModel(commentListDto,boardId);

        return ResponseEntity.ok()
                .body( ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }

    @CustomLogTracer
    @GetMapping("/comments/{commentId}/sub-comments")
    public ResponseEntity getSubComments(@PathVariable Long commentId, CommentSearchCondition condition) {

        SubCommentListDto subCommentListDto = queryRepository.findAllSubCommentByCommentId(commentId, condition);

        EntityModel<SubCommentListDto> entityModel = commentResponseModelAssembler.toListModel(subCommentListDto,commentId);

        return ResponseEntity
                .ok()
                .body( ApiResponse.success(ApiResponseStatus.SUCCESS, entityModel));
    }
}
