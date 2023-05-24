package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponse;
import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.custom_exception.JwtAuthenticationExceptionBasic;
import com.study.badrequest.exception.custom_exception.MemberExceptionBasic;
import com.study.badrequest.exception.custom_exception.TokenExceptionBasic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvisor {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity exception(HttpServletRequest request, Exception e) {
        log.debug("[ExceptionAdvisor.exception]");
        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e,request.getRequestURI()));
    }

    @ExceptionHandler(CustomRuntimeException.class)
    public final ResponseEntity<ApiResponse.Error> customRuntimeException(HttpServletRequest request, CustomRuntimeException e) {
        log.debug("ExceptionAdvisor CustomRuntimeException Exception status: {}, code: {}, message: {}", e.getStatus(), e.getStatus().getCode(), e.getStatus().getMessage());

        return ResponseEntity
                .status(e.getStatus().getHttpStatus())
                .body(ApiResponse.error(e,request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        log.debug("[ExceptionAdvisor.illegalArgumentException]");
        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(e,request.getRequestURI()));
    }
}
