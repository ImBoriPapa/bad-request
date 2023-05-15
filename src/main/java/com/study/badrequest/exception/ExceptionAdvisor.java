package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponse;
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
        log.info("[ExceptionAdvisor.exception]");
        e.printStackTrace();
        ApiResponse.Error error = new ApiResponse.Error(e, request.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(CustomRuntimeException.class)
    public final ResponseEntity<ApiResponse.Error> customRuntimeException(HttpServletRequest request, CustomRuntimeException e) {

        log.info("ExceptionAdvisor CustomRuntimeException Exception status: {}, code: {}, message: {}", e.getStatus(), e.getStatus().getCode(), e.getStatus().getMessage());

        return ResponseEntity
                .status(e.getStatus().getHttpStatus())
                .body(new ApiResponse.Error(e, request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        log.info("[ExceptionAdvisor.illegalArgumentException]");
        e.printStackTrace();
        ApiResponse.Error error = new ApiResponse.Error(e, request.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MemberExceptionBasic.class)
    public final ResponseEntity memberException(HttpServletRequest request, MemberExceptionBasic e) {
        log.info("[ExceptionAdvisor.memberException]");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse.Error(e, request.getRequestURI()));
    }

    @ExceptionHandler(JwtAuthenticationExceptionBasic.class)
    public final ResponseEntity jwtAuthenticationException(HttpServletRequest request, JwtAuthenticationExceptionBasic e) {
        log.info("[ExceptionAdvisor.jwtAuthenticationException]");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse.Error(e, request.getRequestURI()));
    }

    @ExceptionHandler(TokenExceptionBasic.class)
    public final ResponseEntity tokenException(HttpServletRequest request, TokenExceptionBasic e) {
        log.info("[ExceptionAdvisor.tokenException]");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse.Error(e, request.getRequestURI()));
    }
}
