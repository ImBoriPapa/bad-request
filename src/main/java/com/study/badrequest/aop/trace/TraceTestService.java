package com.study.badrequest.aop.trace;

import org.springframework.stereotype.Service;

@Service
public class TraceTestService {
    @CustomLog
    public void logTest(String args){

    }
}
