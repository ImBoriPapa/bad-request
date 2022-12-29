package com.study.badrequest.commons.exception;

import com.study.badrequest.commons.consts.CustomStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

public class CustomMemberException extends BasicException {

    public CustomMemberException(String message) {
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

    public CustomMemberException() {
        super();
    }

    public CustomMemberException(CustomStatus status) {
        super(status);
    }

    public CustomMemberException(CustomStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
