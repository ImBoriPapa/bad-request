package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class CustomValidationException extends BasicException {
    public CustomValidationException(String message) {
        super(message);
    }

    @Override
    public String getStatus() {
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

    public CustomValidationException() {
        super();
    }

    public CustomValidationException(CustomStatus status) {
        super(status);
    }

    public CustomValidationException(CustomStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
