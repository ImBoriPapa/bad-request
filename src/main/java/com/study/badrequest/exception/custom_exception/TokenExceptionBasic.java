package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicCustomException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class TokenExceptionBasic extends BasicCustomException {
    public TokenExceptionBasic(String message) {
        super(message);
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

    public TokenExceptionBasic() {
        super();
    }

    public TokenExceptionBasic(ApiResponseStatus status) {
        super(status);
    }

    public TokenExceptionBasic(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
