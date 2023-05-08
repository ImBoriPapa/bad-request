package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicException;
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

    public MemberException(ApiResponseStatus status) {
        super(status);
    }

    public MemberException(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
