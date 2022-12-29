package com.study.badrequest.exception;

import com.study.badrequest.consts.CustomStatus;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class BasicException extends RuntimeException {

    public BasicException(String message) {
        super(message);
    }

    private CustomStatus status;

    private int errorCode;
    private List<String> errorMessage;

    public CustomStatus getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    public BasicException() {
    }

    public BasicException(CustomStatus status) {
        super(status.getMessage());
        this.status = status;
        this.errorCode = status.getCode();
        this.errorMessage = List.of(status.getMessage());
    }

    public BasicException(CustomStatus status, BindingResult bindingResult) {
        super(status.getMessage());
        this.status = status;
        this.errorCode = status.getCode();
        this.errorMessage = getErrorList(bindingResult);
    }

    private static List<String> getErrorList(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(MessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
    }
}
