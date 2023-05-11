package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicCustomException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class MemberExceptionBasic extends BasicCustomException {

    public MemberExceptionBasic(String message) {
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

    public MemberExceptionBasic() {
        super();
    }

    public MemberExceptionBasic(ApiResponseStatus status) {
        super(status);
    }

    public MemberExceptionBasic(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
