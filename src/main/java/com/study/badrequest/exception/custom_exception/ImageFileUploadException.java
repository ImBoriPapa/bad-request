package com.study.badrequest.exception.custom_exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.BasicException;
import org.springframework.validation.BindingResult;

import java.util.List;

public class ImageFileUploadException extends BasicException {
    public ImageFileUploadException(String message) {
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

    public ImageFileUploadException() {
        super();
    }

    public ImageFileUploadException(ApiResponseStatus status) {
        super(status);
    }

    public ImageFileUploadException(ApiResponseStatus status, BindingResult bindingResult) {
        super(status, bindingResult);
    }
}
