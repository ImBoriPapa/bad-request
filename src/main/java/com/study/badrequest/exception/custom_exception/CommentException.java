package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class CommentException extends BasicException {

    public CommentException() {
        super();
    }

    public CommentException(String message) {
        super(message);
    }

    public CommentException(ApiResponseStatus status) {
        super(status);
    }

    public CommentException(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
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
}
