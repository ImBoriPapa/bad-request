package com.study.badrequest.api.question;

import com.study.badrequest.commons.response.ResponseForm;
import com.study.badrequest.domain.member.Authority;
import com.study.badrequest.domain.question.RecommendationKind;
import com.study.badrequest.dto.question.QuestionRequest;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import static com.study.badrequest.commons.response.ApiResponseStatus.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionApiController {

    private final QuestionService questionService;

    @PostMapping("/api/v2/questions")
    public ResponseEntity create(@RequestBody QuestionRequest.CreateForm form) {

        QuestionResponse.Create create = questionService.creteQuestion(3L, Authority.MEMBER, form);

        return ResponseEntity.ok()
                .body(new ResponseForm.Of<>(SUCCESS, create));
    }

    @PostMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity recommendation(@PathVariable Long questionId,
                                         @RequestParam(required = true,defaultValue = "true") Boolean recommend) {

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
