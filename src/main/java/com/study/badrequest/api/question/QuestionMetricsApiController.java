package com.study.badrequest.api.question;

import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.domain.recommendation.RecommendationKind;
import com.study.badrequest.dto.question.QuestionResponse;
import com.study.badrequest.service.question.QuestionMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.study.badrequest.commons.response.ApiResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionMetricsApiController {

    private final QuestionMetricsService questionMetricsService;

    @PostMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity recommendation(@PathVariable Long questionId,
                                         @RequestParam(required = true, defaultValue = "true") Boolean recommend) {

        RecommendationKind kind = recommend ? RecommendationKind.RECOMMENDATION : RecommendationKind.UN_RECOMMENDATION;

        QuestionResponse.Modify modify = questionMetricsService.createRecommendation(10L, questionId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, modify));
    }

    @DeleteMapping("/api/v2/questions/{questionId}/recommendations")
    public ResponseEntity deleteRecommendation(@PathVariable Long questionId) {

        QuestionResponse.Modify modify = questionMetricsService.deleteRecommendation(10L, questionId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(SUCCESS, modify));
    }
}
