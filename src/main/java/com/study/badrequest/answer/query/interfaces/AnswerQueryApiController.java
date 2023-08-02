package com.study.badrequest.answer.query.interfaces;

import com.study.badrequest.common.status.ExposureStatus;
import com.study.badrequest.answer.query.dao.AnswerQueryRepositoryImpl;
import com.study.badrequest.answer.query.dto.AnswerResult;
import com.study.badrequest.answer.query.dto.AnswerSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnswerQueryApiController {

    private final AnswerQueryRepositoryImpl answerQueryRepository;
    @GetMapping("/api/v2/questions/{questionId}/answers")
    public ResponseEntity getAnswers(@PathVariable Long questionId, AnswerSearchCondition condition) {

        AnswerResult result = answerQueryRepository.findAnswerByQuestionId(questionId, condition.getLastOfData(), ExposureStatus.PUBLIC,null);

        return null;
    }
}
