package com.study.badrequest.exception;

import com.study.badrequest.commons.consts.CustomStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

public class TokenException extends BasicException {
    public TokenException(String message) {
        super(message);
    }

    @Override
    public CustomStatus getStatus() {
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
