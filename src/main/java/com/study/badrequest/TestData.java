package com.study.badrequest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


@Component
@Slf4j
@RequiredArgsConstructor
public class TestData {

    @PostConstruct
    public void init() {
        data();
    }

    @Transactional
    public void data() {
        log.info("INIT DATA");


        log.info("INIT DATA FINISH");
    }
}
