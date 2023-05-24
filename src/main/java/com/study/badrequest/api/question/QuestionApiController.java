package com.study.badrequest.api.question;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.service.questionMetrics.QuestionMetricsService;
import com.study.badrequest.service.question.QuestionService;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


import static com.study.badrequest.commons.constants.ApiURL.*;
import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionApiController {
    private final QuestionService questionService;
    private final QuestionModelAssembler modelAssembler;

    @PostMapping(value = QUESTION_BASE_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody @Valid QuestionRequest.Create form,
                                 BindingResult bindingResult,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("Question Create Request");

        if (bindingResult.hasErrors()) {
            throw new CustomRuntimeException(VALIDATION_ERROR, bindingResult);
        }

        QuestionResponse.Create response = questionService.createQuestion(information.getId(), form);

        return ResponseEntity
                .created(linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(response.getId(), null, null, null)).toUri())
                .body(ApiResponse.success(modelAssembler.createCreateModel(response)));
    }

    @PatchMapping(value = QUESTION_PATCH_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity modify(@PathVariable Long questionId,
                                 @RequestBody QuestionRequest.Modify form,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("Question Modify Request");
        QuestionResponse.Modify response = questionService.modifyQuestion(information.getId(), questionId, form);

        return ResponseEntity.ok()
                .body(ApiResponse.success(modelAssembler.createModifyModel(response)));
    }

    @DeleteMapping(QUESTION_DELETE_URL)
    public ResponseEntity delete(@PathVariable Long questionId,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("Question Delete Request");
        QuestionResponse.Delete response = questionService.deleteQuestion(information.getId(), questionId);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(modelAssembler.createDeleteModel(response)));
    }


}
