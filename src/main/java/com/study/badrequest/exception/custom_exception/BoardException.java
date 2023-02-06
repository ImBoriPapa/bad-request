package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class BoardException extends BasicException {

    public BoardException() {
        super();
    }

    public BoardException(String message) {
        super(message);
    }

    public BoardException(CustomStatus status) {
        super(status);
    }

    public BoardException(CustomStatus status, BindingResult bindingResult) {
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
