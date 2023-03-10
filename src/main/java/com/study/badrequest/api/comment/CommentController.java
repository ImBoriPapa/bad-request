package com.study.badrequest.api.comment;


import com.study.badrequest.aop.annotation.CustomLogTracer;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.CommentException;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.domain.comment.dto.CommentRequest;
import com.study.badrequest.domain.comment.dto.CommentResponse;
import com.study.badrequest.domain.comment.service.CommentCommendService;
import com.study.badrequest.utils.modelAssembler.CommentResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.study.badrequest.commons.consts.CustomURL.BASE_URL;

@RestController
@Slf4j
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class CommentController {

    // TODO: 2023/02/17 validation test
    private final CommentResponseModelAssembler commentResponseModelAssembler;
    private final CommentCommendService commentCommendService;

    @CustomLogTracer
    @PostMapping("/board/{boardId}/comments")
    public ResponseEntity postComments(@Valid @AuthenticationPrincipal User user,
                                       @PathVariable Long boardId,
                                       @RequestBody CommentRequest.Create form,
                                       BindingResult bindingResult) {
        existUsername(user);

        if (bindingResult.hasErrors()) {
            throw new CommentException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        CommentResponse.Create create = commentCommendService.addComment(boardId, user.getUsername(), form);

        EntityModel<CommentResponse.Create> entityModel = commentResponseModelAssembler.toModel(create, boardId);


        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of(CustomStatus.SUCCESS, entityModel));
    }


    @CustomLogTracer
    @PutMapping("/board/{boardId}/comments/{commentId}")
    public ResponseEntity putComments(@Valid
                                      @PathVariable Long boardId,
                                      @PathVariable Long commentId,
                                      @RequestBody CommentRequest.Update form,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CommentException(CustomStatus.VALIDATION_ERROR, bindingResult);
        }

        CommentResponse.Modify modifyComment = commentCommendService.modifyComment(commentId, form.getText());

        EntityModel<CommentResponse.Modify> entityModel = commentResponseModelAssembler.toModel(modifyComment, boardId);

        return ResponseEntity.ok().body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @CustomLogTracer
    @DeleteMapping("/board/{boardId}/comments/{commentId}")
    public ResponseEntity deleteComments(@PathVariable Long boardId,
                                         @PathVariable Long commentId) {

        CommentResponse.Delete deleteComment = commentCommendService.deleteComment(commentId);

        EntityModel<CommentResponse.Delete> entityModel = commentResponseModelAssembler.toModel(deleteComment, boardId);

        return ResponseEntity.ok().body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @CustomLogTracer
    @PostMapping("/comments/{commentId}/sub-comments")
    public ResponseEntity postSubComments(@AuthenticationPrincipal User user,
                                          @PathVariable Long commentId,
                                          @RequestBody CommentRequest.Create form) {
        existUsername(user);

        CommentResponse.CreateSub result = commentCommendService.addSubComment(commentId, user.getUsername(), form);

        EntityModel<CommentResponse.CreateSub> entityModel = commentResponseModelAssembler.toModel(result, commentId);

        return ResponseEntity
                .ok()
                .body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @CustomLogTracer
    @PutMapping("/comments/{commentId}/sub-comments/{subCommentId}")
    public ResponseEntity putSubComments(@PathVariable Long commentId,
                                         @PathVariable Long subCommentId,
                                         @RequestBody CommentRequest.Update form) {

        CommentResponse.ModifySub result = commentCommendService.modifySubComment(subCommentId, form.getText());

        EntityModel<CommentResponse.ModifySub> entityModel = commentResponseModelAssembler.toModel(result, commentId);

        return ResponseEntity.ok().body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    @CustomLogTracer
    @DeleteMapping("/comments/{commentId}/sub-comments/{subCommentId}")
    public ResponseEntity deleteSubComments(@PathVariable Long commentId,
                                            @PathVariable Long subCommentId) {

        CommentResponse.DeleteSub result = commentCommendService.deleteSubComment(subCommentId);

        EntityModel<CommentResponse.DeleteSub> entityModel = commentResponseModelAssembler.toModel(result, commentId);

        return ResponseEntity.ok().body(new ResponseForm.Of<>(CustomStatus.SUCCESS, entityModel));
    }

    /**
     * Username ??????
     */
    private void existUsername(User user) {
        if (user.getUsername() == null || user.getUsername().equals("anonymousUser")) {
            throw new CommentException(CustomStatus.PERMISSION_DENIED);
        }
    }
}
