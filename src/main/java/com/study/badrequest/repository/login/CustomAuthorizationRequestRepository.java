package com.study.badrequest.repository.login;

import com.study.badrequest.utils.cookie.CookieFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@Slf4j
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URL_PARAM_COOKIE_NAME = "redirect_url"; //client와 맞춰야함
    private static final int cookieExpireSeconds = 180;
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        log.info("loadAuthorizationRequest");
        return CookieFactory.getCookie(request,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieFactory.deserialize(cookie,OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("saveAuthorizationRequest");
        if (authorizationRequest == null) {
            CookieFactory.deleteCookie(request,response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieFactory.deleteCookie(request,response, REDIRECT_URL_PARAM_COOKIE_NAME);
            return;
        }
        CookieFactory.addCookie(response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,CookieFactory.serialize(authorizationRequest),cookieExpireSeconds);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URL_PARAM_COOKIE_NAME);
        log.info("redirectUriAfterLogin: {}",redirectUriAfterLogin);

        if (StringUtils.hasLength(redirectUriAfterLogin)) {
            CookieFactory.addCookie(response, REDIRECT_URL_PARAM_COOKIE_NAME,redirectUriAfterLogin,cookieExpireSeconds);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieFactory.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieFactory.deleteCookie(request, response, REDIRECT_URL_PARAM_COOKIE_NAME);
    }
}