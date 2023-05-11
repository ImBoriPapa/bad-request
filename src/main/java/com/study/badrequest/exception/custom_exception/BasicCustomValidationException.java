package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicCustomException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class BasicCustomValidationException extends BasicCustomException {
    public BasicCustomValidationException(String message) {
        super(message);
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

    public BasicCustomValidationException() {
        super();
    }

    public BasicCustomValidationException(ApiResponseStatus status) {
        super(status);
    }

    public BasicCustomValidationException(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
