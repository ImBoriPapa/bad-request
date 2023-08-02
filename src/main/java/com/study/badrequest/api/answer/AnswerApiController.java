package com.study.badrequest.api.answer;

import com.study.badrequest.common.annotation.LoggedInMember;
import com.study.badrequest.common.response.ApiResponse;
import com.study.badrequest.member.command.domain.CurrentMember;
import com.study.badrequest.dto.answer.AnswerRequest;
import com.study.badrequest.dto.answer.AnswerResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.answer.command.application.AnswerService;
import com.study.badrequest.utils.modelAssembler.AnswerModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.study.badrequest.common.constants.ApiURL.ANSWER_REGISTER;
import static com.study.badrequest.common.response.ApiResponseStatus.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnswerApiController {
    private final AnswerService answerService;
    private final AnswerModelAssembler modelAssembler;

    @PostMapping(value = ANSWER_REGISTER, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity register(@PathVariable Long questionId,
                                   @LoggedInMember CurrentMember.Information information,
                                   @Validated @RequestBody AnswerRequest.Register request, BindingResult bindingResult) {
        log.info("Create Answer Request");

        if(bindingResult.hasErrors()){
            throw CustomRuntimeException.createWithApiResponseStatus(VALIDATION_ERROR);
        }

        AnswerResponse.Register register = answerService.createAnswer(information.getId(), questionId, request);

        return ResponseEntity
                .created(linkTo(methodOn(AnswerQueryApiController.class).getAnswers(questionId,null)).toUri())
                .body(ApiResponse.success(modelAssembler.createAnswerRegisterModel(questionId, register)));
    }

    @PatchMapping("/api/v2/answers/{answerId}")
    public ResponseEntity modify(@PathVariable Long answerId,
                                 @LoggedInMember CurrentMember.Information information,
                                 @RequestBody AnswerRequest.Modify form
    ) {

        AnswerResponse.Modify response = answerService.modifyAnswer(information.getId(), answerId, form);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response));
    }

    @DeleteMapping("/api/v2/answers/{answerId}")
    public ResponseEntity delete(@PathVariable Long answerId,
                                 @LoggedInMember CurrentMember.Information information) {

        AnswerResponse.Delete response = answerService.deleteAnswer(information.getId(), answerId);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response));
    }

}
