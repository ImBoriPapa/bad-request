package com.study.badrequest.api.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AnswerQueryApiController {

    @GetMapping("/api/v2/questions/{questionId}/answers/{answerId}")
    public ResponseEntity getOne(@PathVariable Long questionId, @PathVariable Long answerId) {

        return null;
    }
}
