package com.study.badrequest.utils.authentication;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.custom_exception.CustomOauth2LoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.study.badrequest.commons.constants.ApiURL.OAUTH2_LOGIN_FAILURE_WITH_CODE;

@Component
@Slf4j
public class Oauth2AuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        ApiResponseStatus apiResponseStatus = ((CustomOauth2LoginException) exception).getApiResponseStatus();

        response.sendRedirect(OAUTH2_LOGIN_FAILURE_WITH_CODE + apiResponseStatus.getCode());
    }
}
