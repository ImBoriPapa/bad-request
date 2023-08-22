package com.study.badrequest.question.command.interfaces;

import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.login.command.domain.CurrentMember;
import com.study.badrequest.question.command.application.QuestionCreateService;
import com.study.badrequest.question.query.interfaces.QuestionRequest;
import com.study.badrequest.common.exception.CustomRuntimeException;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


import static com.study.badrequest.common.constants.ApiURL.*;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionApiController {
    private final QuestionCreateService questionCreateService;
    private final QuestionModelAssembler modelAssembler;

    @PostMapping(value = QUESTION_BASE_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity createQuestion(@RequestBody @Valid QuestionRequest.Create form, BindingResult bindingResult,
                                         @LoggedInMember CurrentMember.Information information) {
        log.info("Question Create Request");

        if (bindingResult.hasErrors()) {
            throw CustomRuntimeException.createWithBindingResults(VALIDATION_ERROR, bindingResult);
        }

//        QuestionResponse.Create response = questionService.createQuestionProcessing(information.getId(), form);

        return ResponseEntity.ok()
//                .created(linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(response.getId(), null, null, null)).toUri())
                .body(ApiResponse.success(modelAssembler.createCreateModel(null)));
    }

    @PatchMapping(value = QUESTION_PATCH_URL, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity modify(@PathVariable Long questionId,
                                 @RequestBody QuestionRequest.Modify form,
                                 @LoggedInMember CurrentMember.Information information) {
        log.info("Question Modify Request");
//        QuestionResponse.Modify response = questionService.modifyQuestionProcessing(information.getId(), questionId, form);

        return ResponseEntity.ok()
                .body(ApiResponse.success(modelAssembler.createModifyModel(null)));
    }

    @DeleteMapping(QUESTION_DELETE_URL)
    public ResponseEntity delete(@PathVariable Long questionId,
                                 @LoggedInMember CurrentMember.Information information) {
        log.info("Question Delete Request");
//        QuestionResponse.Delete response = questionService.deleteQuestionProcess(information.getId(), questionId);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(modelAssembler.createDeleteModel(null)));
    }


}
