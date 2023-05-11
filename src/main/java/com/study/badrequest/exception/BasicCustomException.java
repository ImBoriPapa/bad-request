package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class BasicCustomException extends RuntimeException {

    public BasicCustomException() {
    }

    public BasicCustomException(String message) {
        super(message);
    }

    public BasicCustomException(ApiResponseStatus status) {
        super(status.getMessage());
        this.status = status;
        this.errorCode = status.getCode();
        this.errorMessage = List.of(status.getMessage());
    }

    public BasicCustomException(ApiResponseStatus status, BindingResult bindingResult) {
        super(status.getMessage());
        this.status = status;
        this.errorCode = status.getCode();
        this.errorMessage = getErrorList(bindingResult);
    }

    private ApiResponseStatus status;

    private int errorCode;

    private List<String> errorMessage;

    public ApiResponseStatus getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    private static List<String> getErrorList(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
