package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicCustomException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class BoardExceptionBasic extends BasicCustomException {

    public BoardExceptionBasic() {
        super();
    }

    public BoardExceptionBasic(String message) {
        super(message);
    }

    public BoardExceptionBasic(ApiResponseStatus status) {
        super(status);
    }

    public BoardExceptionBasic(ApiResponseStatus status, BindingResult bindingResult) {
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
