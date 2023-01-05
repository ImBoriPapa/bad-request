package com.study.badrequest.exception;

import com.study.badrequest.commons.consts.CustomStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

public class MemberException extends BasicException {

    public MemberException(String message) {
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

    public MemberException() {
        super();
    }

    public MemberException(CustomStatus status) {
        super(status);
    }

    public MemberException(CustomStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
