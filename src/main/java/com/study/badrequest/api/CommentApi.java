package com.study.badrequest.api;

import com.study.badrequest.aop.annotation.CustomLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentApi {

    @CustomLogger
    @GetMapping("/api/v1/comments")
    public void getComment() {

    }
}
