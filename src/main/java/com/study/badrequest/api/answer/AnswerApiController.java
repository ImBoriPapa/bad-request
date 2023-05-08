package com.study.badrequest.api.answer;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.commons.response.ResponseForm;
import com.study.badrequest.domain.question.Answer;
import com.study.badrequest.repository.answer.query.AnswerDto;
import com.study.badrequest.repository.answer.query.AnswerQueryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@RestController
@RequiredArgsConstructor
public class AnswerApiController {

    private final AnswerQueryRepositoryImpl queryRepository;

    @GetMapping("/api/v2/questions/{questionId}/answers")
    private ResponseEntity getAnswers(@PathVariable Long questionId) {

        List<AnswerDto> dtos = queryRepository.findAnswerByQuestionId(questionId);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(SUCCESS, dtos));
    }
}
