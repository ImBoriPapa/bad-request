package com.study.badrequest.domain.log.service;


import com.study.badrequest.aop.annotation.CustomLogTracer;
import org.springframework.stereotype.Service;

@Service
public class TraceTestService {
    @CustomLogTracer
    public void logTest(String args){

    }

    @CustomLogTracer
    public void logExTest(String args){
        throw new IllegalArgumentException("[EXCEPTION]");
    }
}
