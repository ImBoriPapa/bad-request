package com.study.badrequest.api.question;

import com.study.badrequest.commons.annotation.LoggedInMember;
import com.study.badrequest.commons.response.ResponseForm;
import com.study.badrequest.domain.login.CurrentLoggedInMember;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.question.RecommendationKind;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionApiController {
    private final QuestionService questionService;

    @PostMapping(value = "/api/v2/questions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody @Valid QuestionRequest.CreateForm form,
                                 BindingResult bindingResult,
                                 @LoggedInMember CurrentLoggedInMember.Information information) {
        log.info("질문 생성 요청자 아이디: {}", information.getId());

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors() + "");
        }

        QuestionResponse.Create create = questionService.creteQuestion(information.getId(), form);


        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(SUCCESS, create));
    }

    @PostMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity recommendation(@PathVariable Long questionId,
                                         @RequestParam(required = true, defaultValue = "true") Boolean recommend) {

        RecommendationKind kind = recommend ? RecommendationKind.RECOMMENDATION : RecommendationKind.UN_RECOMMENDATION;

        QuestionResponse.Modify modify = questionService.createRecommendation(10L, Authority.MEMBER, questionId, kind);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(SUCCESS, modify));
    }

    @DeleteMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity deleteRecommendation(@PathVariable Long questionId) {

        QuestionResponse.Modify modify = questionService.deleteRecommendation(10L, Authority.MEMBER, questionId);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(SUCCESS, modify));
    }
}
