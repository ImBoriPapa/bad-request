package com.study.badrequest.commons.exception.custom_exception;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class CommentException extends BasicException {

    public CommentException() {
        super();
    }

    public CommentException(String message) {
        super(message);
    }

    public CommentException(CustomStatus status) {
        super(status);
    }

    public CommentException(CustomStatus status, BindingResult bindingResult) {
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
