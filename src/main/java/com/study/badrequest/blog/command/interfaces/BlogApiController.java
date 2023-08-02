package com.study.badrequest.blog.command.interfaces;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BlogApiController {

    @GetMapping("/api/v2/blog/{location}")
    public void get1(@PathVariable String location){

    }

}
