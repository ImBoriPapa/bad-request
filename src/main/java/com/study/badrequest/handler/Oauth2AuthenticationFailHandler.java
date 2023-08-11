package com.study.badrequest.handler;


import com.study.badrequest.common.response.ApiResponseStatus;

import com.study.badrequest.common.exception.CustomOauth2LoginException;

import com.study.badrequest.member.command.domain.CustomAuthorizationRequestRepository;
import com.study.badrequest.utils.cookie.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.study.badrequest.member.command.domain.CustomAuthorizationRequestRepository.REDIRECT_URL_PARAM_COOKIE_NAME;


@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2AuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final CustomAuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("Oauth2AuthenticationFailHandler");

        ApiResponseStatus apiResponseStatus = ((CustomOauth2LoginException) exception).getApiResponseStatus();

        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URL_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", apiResponseStatus.getCode())
                .build().toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
