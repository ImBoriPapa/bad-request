package com.study.badrequest.exception;

import com.study.badrequest.commons.form.ResponseForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvisor {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity exception(HttpServletRequest request, Exception e) {
        log.info("[ExceptionAdvisor.exception]");
        e.printStackTrace();
        ResponseForm.Error error = new ResponseForm.Error(e, request.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BasicException.class)
    public final ResponseEntity basicException(HttpServletRequest request,BasicException e){
        log.info("[ExceptionAdvisor.exception]");
        ResponseForm.Error error = new ResponseForm.Error(e, request.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }
}
