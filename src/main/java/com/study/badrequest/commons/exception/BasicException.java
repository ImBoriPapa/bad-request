package com.study.badrequest.commons.exception;

import com.study.badrequest.commons.consts.CustomStatus;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class BasicException extends RuntimeException {

    public BasicException() {
    }

    public BasicException(String message) {
        super(message);
    }

    public BasicException(CustomStatus status) {
        super(status.getMessage());
        this.status = status.name();
        this.errorCode = status.getCode();
        this.errorMessage = List.of(status.getMessage());
    }

    public BasicException(CustomStatus status, BindingResult bindingResult) {
        super(status.getMessage());
        this.status = status.name();
        this.errorCode = status.getCode();
        this.errorMessage = getErrorList(bindingResult);
    }

    private String status;

    private int errorCode;

    private List<String> errorMessage;

    public String getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    private static List<String> getErrorList(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
