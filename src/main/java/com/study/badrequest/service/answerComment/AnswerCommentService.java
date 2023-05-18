package com.study.badrequest.service.answerComment;

import com.study.badrequest.dto.answerComment.AnswerCommentRequest;
import com.study.badrequest.dto.answerComment.AnswerCommentResponse;

public interface AnswerCommentService {

    AnswerCommentResponse.Add addComment(Long memberId, Long answerId, AnswerCommentRequest.Add form);

    AnswerCommentResponse.Delete deleteComment(Long memberId, Long answerId);
}
