package com.study.badrequest.answer.command.application;

import com.study.badrequest.answer.command.interfaces.AnswerCommentRequest;
import com.study.badrequest.answer.command.interfaces.AnswerCommentResponse;

public interface AnswerCommentService {

    AnswerCommentResponse.Add addComment(Long memberId, Long answerId, AnswerCommentRequest.Add form);

    AnswerCommentResponse.Delete deleteComment(Long memberId, Long answerId);
}
