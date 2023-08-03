package com.study.badrequest.common.exception;

import com.study.badrequest.common.response.ApiResponseStatus;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

public class CustomRuntimeException extends RuntimeException {

    private HttpStatus httpStatus;

    private String status;

    private int errorCode;

    private String errorMessage;

    private CustomRuntimeException() {
    }

    private CustomRuntimeException(HttpStatus httpStatus, String status, int errorCode, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static CustomRuntimeException createWithApiResponseStatus(ApiResponseStatus status) {
        return new CustomRuntimeException(status.getHttpStatus(), status.name(), status.getCode(), status.getMessage());
    }

    public static CustomRuntimeException createWithApiResponseStatusAndMessage(ApiResponseStatus status,String message) {
        return new CustomRuntimeException(status.getHttpStatus(), status.name(), status.getCode(), message);
    }

    public static CustomRuntimeException createWithBindingResults(ApiResponseStatus status, BindingResult bindingResult) {
        return new CustomRuntimeException(status.getHttpStatus(), status.name(), status.getCode(), extractErrorMessageFromBindingResults(bindingResult));
    }

    private static String extractErrorMessageFromBindingResults(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream().map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList())
                .toString();
    }

    public HttpStatus gethttpStatus() {
        return httpStatus;
    }

    public String getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}
