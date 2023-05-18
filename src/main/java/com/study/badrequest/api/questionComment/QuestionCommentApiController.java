package com.study.badrequest.api.questionComment;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.dto.questionComment.QuestionCommentRequest;
import com.study.badrequest.dto.questionComment.QuestionCommentResponse;
import com.study.badrequest.service.questionComment.QuestionCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionCommentApiController {

    private final QuestionCommentService questionCommentService;

    @PostMapping("/api/v2/questions/{questionId}")
    public ResponseEntity add(@PathVariable Long questionId, @LoggedInMember CurrentLoggedInMember.Information information,
                              @Validated @RequestBody QuestionCommentRequest.Add form, BindingResult bindingResult) {
        log.info("질문 댓글 추가 요청");

        QuestionCommentResponse.Add response = questionCommentService.addComment(information.getId(), questionId, form);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response));

    }

    @DeleteMapping("/api/v2/question-comments/{commentId}")
    public ResponseEntity delete(@PathVariable Long commentId, @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("질문 댓글 삭제 요청");

        QuestionCommentResponse.Delete response = questionCommentService.deleteComment(information.getId(), commentId);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response));
    }
}
