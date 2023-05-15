package com.study.badrequest.api.question;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.question.RecommendationKind;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.exception.CustomRuntimeException;
import com.study.badrequest.service.question.QuestionService;
import com.study.badrequest.utils.modelAssembler.QuestionModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


import static com.study.badrequest.commons.response.ApiResponseStatus.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionApiController {
    private final QuestionService questionService;
    private final QuestionModelAssembler modelAssembler;

    @PostMapping(value = "/api/v2/questions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody @Valid QuestionRequest.CreateForm form,
                                 BindingResult bindingResult,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("질문 생성 요청- 요청자 아이디: {}", information.getId());

        if (bindingResult.hasErrors()) {
            throw new CustomRuntimeException(VALIDATION_ERROR, bindingResult);
        }

        QuestionResponse.Create response = questionService.creteQuestion(information.getId(), form);

        return ResponseEntity
                .created(linkTo(methodOn(QuestionQueryApiController.class).getQuestionDetail(response.getId(), null, null, null)).toUri())
                .body(new ApiResponse.Success<>(SUCCESS, modelAssembler.createCreateModel(response)));
    }

    @PatchMapping("/api/v2/questions/{questionId}")
    public ResponseEntity modify(@PathVariable Long questionId,
                                 @RequestBody QuestionRequest.ModifyForm form,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {

        QuestionResponse.Modify response = questionService.modifyQuestion(information.getId(), questionId, form);

        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping("/api/v2/questions/{questionId}")
    public ResponseEntity delete(@PathVariable Long questionId,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("질문 삭제 요청 ");
        QuestionResponse.Delete response = questionService.deleteQuestion(information.getId(), questionId);

        return ResponseEntity
                .ok()
                .body(response);
    }


    @PostMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity recommendation(@PathVariable Long questionId,
                                         @RequestParam(required = true, defaultValue = "true") Boolean recommend) {

        RecommendationKind kind = recommend ? RecommendationKind.RECOMMENDATION : RecommendationKind.UN_RECOMMENDATION;

        QuestionResponse.Modify modify = questionService.createRecommendation(10L, Authority.MEMBER, questionId, kind);

        return ResponseEntity.ok()
                .body(new ApiResponse.Success<>(SUCCESS, modify));
    }

    @DeleteMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity deleteRecommendation(@PathVariable Long questionId) {

        QuestionResponse.Modify modify = questionService.deleteRecommendation(10L, Authority.MEMBER, questionId);

        return ResponseEntity.ok()
                .body(new ApiResponse.Success<>(SUCCESS, modify));
    }
}
