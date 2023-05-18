package com.study.badrequest.service.question;

import com.study.badrequest.dto.questionComment.QuestionCommentRequest;
import com.study.badrequest.dto.questionComment.QuestionCommentResponse;

public interface QuestionCommentService {

    QuestionCommentResponse.Add addComment(Long memberId, Long questionId, QuestionCommentRequest.Add form);

    QuestionCommentResponse.Delete deleteComment(Long memberId, Long questionCommentId);
}
