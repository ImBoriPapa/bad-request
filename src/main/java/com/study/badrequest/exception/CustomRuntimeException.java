package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

public class CustomRuntimeException extends BasicCustomException{
    public CustomRuntimeException() {
        super();
    }

    public CustomRuntimeException(String message) {
        super(message);
    }

    public CustomRuntimeException(ApiResponseStatus status) {
        super(status);
    }

    public CustomRuntimeException(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }

    @Override
    public ApiResponseStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode();
    }

    @Override
    public List<String> getErrorMessage() {
        return super.getErrorMessage();
    }
}
