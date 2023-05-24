package com.study.badrequest.handler;

import com.study.badrequest.domain.login.MemberPrincipal;
import com.study.badrequest.repository.login.CustomAuthorizationRequestRepository;
import com.study.badrequest.service.login.LoginService;
import com.study.badrequest.utils.cookie.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;


import static com.study.badrequest.repository.login.CustomAuthorizationRequestRepository.REDIRECT_URL_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${oauth.authorizedRedirectUris}")
    private String redirectUrl;
    private final CustomAuthorizationRequestRepository authorizationRequestRepository;
    private final LoginService loginService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        MemberPrincipal principal = (MemberPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String temporaryAuthenticationCode = loginService.getOneTimeAuthenticationCode(principal.getMemberId());

        String targetUrl = determineTargetUrl(request, response, authentication, temporaryAuthenticationCode);

        clearAuthenticationAttributes(request, response);

        if (response.isCommitted()) {
            log.info("응답이 이미 커밋되었습니다.");
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication, String temporaryCode) {

        Optional<String> redirectUrl = CookieUtils.getCookie(request, REDIRECT_URL_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUrl.isPresent() && !isAuthorizedRedirectUri(redirectUrl.get())) {
            throw new IllegalArgumentException("리다이렉트 URI 안맞음");
        }

        return UriComponentsBuilder
                .fromUriString(redirectUrl.orElse(getDefaultTargetUrl()))
                .queryParam("authentication_code", temporaryCode)
                .build()
                .toUriString();

    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUrl);

        if (clientRedirectUri.getHost().equalsIgnoreCase(authorizedUri.getHost())) {
            return true;
        }
        return false;
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
