package com.study.badrequest.question.command.application;

import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;

public interface QuestionService {

    QuestionResponse.Create createQuestionProcessing(Long memberId, QuestionRequest.Create form);

    QuestionResponse.Modify modifyQuestionProcessing(Long memberId, Long questionId, QuestionRequest.Modify form);

    QuestionResponse.Delete deleteQuestionProcess(Long memberId, Long questionId);

}
