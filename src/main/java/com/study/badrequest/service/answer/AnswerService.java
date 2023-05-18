package com.study.badrequest.service.answer;

import com.study.badrequest.dto.answer.AnswerRequest;
import com.study.badrequest.dto.answer.AnswerResponse;

public interface AnswerService {

    AnswerResponse.Register registerAnswer(Long memberId, Long questionId, AnswerRequest.Register form);
    AnswerResponse.Modify modifyAnswer(Long memberId, Long answerId, AnswerRequest.Modify form);
    AnswerResponse.Delete deleteAnswer(Long memberId, Long answerId);
}
