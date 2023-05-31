package com.study.badrequest.exception;

import com.study.badrequest.commons.response.ApiResponseStatus;
import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

@Getter
public class CustomOauth2LoginException extends OAuth2AuthenticationException {

    private ApiResponseStatus apiResponseStatus;

    public CustomOauth2LoginException(ApiResponseStatus apiResponseStatus) {
        super(apiResponseStatus.getMessage());
        this.apiResponseStatus = apiResponseStatus;
    }

    public CustomOauth2LoginException(String errorCode) {
        super(errorCode);
    }

    public CustomOauth2LoginException(OAuth2Error error) {
        super(error);
    }

    public CustomOauth2LoginException(OAuth2Error error, Throwable cause) {
        super(error, cause);
    }

    public CustomOauth2LoginException(OAuth2Error error, String message) {
        super(error, message);
    }

    public CustomOauth2LoginException(OAuth2Error error, String message, Throwable cause) {
        super(error, message, cause);
    }

    @Override
    public OAuth2Error getError() {
        return super.getError();
    }

}
