package com.study.badrequest.commons.filter;

import com.study.badrequest.domain.login.service.JwtLoginService;
import com.study.badrequest.utils.jwt.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static com.study.badrequest.commons.consts.JwtTokenHeader.JWT_STATUS_HEADER;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final JwtLoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("JwtAuthenticationFilter");
        String accessToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        JwtStatus jwtStatus;

        jwtStatus = StringUtils.hasText(accessToken) ? jwtUtils.validateToken(accessToken) : JwtStatus.EMPTY_TOKEN;

        statusJwtHandle(request, accessToken, jwtStatus);

        filterChain.doFilter(request, response);
    }

    /**
     * JWT Status 에 따라서 인증 처리
     */
    private void statusJwtHandle(HttpServletRequest request, String accessToken, JwtStatus jwtStatus) {
        switch (jwtStatus) {
            case ACCESS:
                Authentication authentication = jwtUtils.getAuthentication(accessToken);

                if (loginCheck(authentication)) {
                    log.debug("[JwtAuthenticationFilter Set SecurityContextHolder Context]");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    break;
                } else {
                    log.debug("[JwtAuthenticationFilter is Logout token]");
                    jwtStatus = JwtStatus.LOGOUT;
                }

                break;
            case EXPIRED:
                log.debug("[JwtAuthenticationFilter is EXPIRED token]");
                jwtStatus = JwtStatus.EXPIRED;
                break;
            case DENIED:
                log.debug("[JwtAuthenticationFilter is DENIED token]");
                jwtStatus = JwtStatus.DENIED;
                break;
            case EMPTY_TOKEN:
                log.debug("[JwtAuthenticationFilter is EMPTY token]");
                jwtStatus = JwtStatus.EMPTY_TOKEN;
                break;
            default:
                log.debug("[JwtAuthenticationFilter is ERROR token]");
                jwtStatus = JwtStatus.ERROR;
        }
        request.setAttribute(JWT_STATUS_HEADER, jwtStatus);
    }

    private boolean loginCheck(Authentication authentication) {
        log.debug("[JwtAuthenticationFilter.loginCheck]");
        return loginService.loginCheck(authentication.getName());
    }
}


