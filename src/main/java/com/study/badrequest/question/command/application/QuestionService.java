package com.study.badrequest.question.command.application;

import com.study.badrequest.question.query.interfaces.QuestionRequest;
import com.study.badrequest.question.query.interfaces.QuestionResponse;

public interface QuestionService {

    QuestionResponse.Create createQuestionProcessing(Long memberId, QuestionRequest.Create form);

    QuestionResponse.Modify modifyQuestionProcessing(Long memberId, Long questionId, QuestionRequest.Modify form);

    QuestionResponse.Delete deleteQuestionProcess(Long memberId, Long questionId);

}
