package com.study.badrequest.answer.command.application;

import com.study.badrequest.answer.command.interfaces.AnswerRequest;
import com.study.badrequest.answer.command.interfaces.AnswerResponse;

public interface AnswerService {

    AnswerResponse.Register createAnswer(Long memberId, Long questionId, AnswerRequest.Register form);
    AnswerResponse.Modify modifyAnswer(Long memberId, Long answerId, AnswerRequest.Modify form);
    AnswerResponse.Delete deleteAnswer(Long memberId, Long answerId);
}
