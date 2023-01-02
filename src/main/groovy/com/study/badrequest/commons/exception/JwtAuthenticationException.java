package com.study.badrequest.commons.exception;

import com.study.badrequest.commons.consts.CustomStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

public class JwtAuthenticationException extends BasicException{
    public JwtAuthenticationException(String message) {
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

    public JwtAuthenticationException() {
        super();
    }

    public JwtAuthenticationException(CustomStatus status) {
        super(status);
    }

    public JwtAuthenticationException(CustomStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
