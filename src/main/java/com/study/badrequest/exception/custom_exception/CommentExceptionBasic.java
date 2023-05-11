package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicCustomException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class CommentExceptionBasic extends BasicCustomException {

    public CommentExceptionBasic() {
        super();
    }

    public CommentExceptionBasic(String message) {
        super(message);
    }

    public CommentExceptionBasic(ApiResponseStatus status) {
        super(status);
    }

    public CommentExceptionBasic(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }

    @Override
    public ApiResponseStatus getStatus() {
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
