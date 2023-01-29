package com.study.badrequest.domain.log.service;


import com.study.badrequest.aop.annotation.CustomLogger;
import org.springframework.stereotype.Service;

@Service
public class TraceTestService {
    @CustomLogger
    public void logTest(String args){

    }

    @CustomLogger
    public void logExTest(String args){
        throw new IllegalArgumentException("[EXCEPTION]");
    }
}
