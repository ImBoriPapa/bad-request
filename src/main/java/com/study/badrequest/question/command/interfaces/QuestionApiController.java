package com.study.badrequest.question.command.interfaces;

import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.question.command.application.QuestionCreateService;
import com.study.badrequest.question.command.application.dto.CreateQuestionRequest;
import com.study.badrequest.question.command.interfaces.dto.QuestionCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.study.badrequest.common.constants.ApiURL.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionApiController {
    private final QuestionCreateService questionCreateService;

    @PostMapping(value = QUESTION_BASE_URL)
    public ResponseEntity create(@RequestBody CreateQuestionRequest request) {

        Long questionId = questionCreateService.createQuestion(request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(new QuestionCreateResponse(questionId)));
    }


}
