package com.study.badrequest.api.answer;

import com.study.badrequest.commons.status.ExposureStatus;
import com.study.badrequest.repository.answer.query.AnswerDto;
import com.study.badrequest.repository.answer.query.AnswerQueryRepositoryImpl;
import com.study.badrequest.repository.answer.query.AnswerResult;
import com.study.badrequest.repository.answer.query.AnswerSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
