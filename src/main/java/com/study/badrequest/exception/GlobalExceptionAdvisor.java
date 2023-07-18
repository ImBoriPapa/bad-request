package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvisor {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse.Error> handleException(HttpServletRequest request, Exception e) {
        log.info("handle exception Request -> method: {}, uri:{}, message: {}", request.getMethod(), request.getRequestURI(), e.getMessage());

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(e, request.getRequestURI()));
    }

    @ExceptionHandler(CustomRuntimeException.class)
    public final ResponseEntity<ApiResponse.Error> handleCustomRuntimeException(HttpServletRequest request, CustomRuntimeException e) {
        log.info("handle CustomRuntimeException -> \n" +
                        "method: {}, uri: {}" +
                        "status: {}, code: {}, message: {}",
                request.getMethod(), request.getRequestURI(), e.getStatus(), e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(e.gethttpStatus())
                .body(ApiResponse.error(e, request.getRequestURI()));
    }

}
