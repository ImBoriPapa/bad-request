package com.study.badrequest.commons.exception.custom_exception;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class TokenException extends BasicException {
    public TokenException(String message) {
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

    public TokenException() {
        super();
    }

    public TokenException(CustomStatus status) {
        super(status);
    }

    public TokenException(CustomStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
