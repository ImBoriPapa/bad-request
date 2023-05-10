package com.study.badrequest.service.question;

import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.question.ExposureStatus;
import com.study.badrequest.domain.question.RecommendationKind;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;

public interface QuestionService {

    QuestionResponse.Create creteQuestion(Long memberId, QuestionRequest.CreateForm form);
    QuestionResponse.Modify modifyQuestion(Long memberId, Authority authority, Long questionId, QuestionRequest.ModifyForm form);
    QuestionResponse.Delete deleteQuestion(Long memberId, Authority authority, Long questionId);
    void incrementViewCount(Long questionId , ExposureStatus exposureStatus);
    QuestionResponse.Modify createRecommendation(Long memberId, Authority authority, Long questionId, RecommendationKind recommendationKind);
    QuestionResponse.Modify deleteRecommendation(Long memberId, Authority authority, Long questionId);
}
