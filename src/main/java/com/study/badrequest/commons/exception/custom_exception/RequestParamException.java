package com.study.badrequest.commons.exception.custom_exception;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class RequestParamException extends BasicException {

    public RequestParamException(String message) {
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

    public RequestParamException() {
        super();
    }

    public RequestParamException(CustomStatus status) {
        super(status);
    }

    public RequestParamException(CustomStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
