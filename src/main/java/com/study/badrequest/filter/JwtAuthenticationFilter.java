package com.study.badrequest.filter;

import com.study.badrequest.service.login.LoginServiceImpl;
import com.study.badrequest.commons.status.JwtStatus;
import com.study.badrequest.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.study.badrequest.commons.constants.AuthenticationHeaders.JWT_STATUS_HEADER;
import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final LoginServiceImpl loginServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request URI= {}", request.getRequestURI());

        String accessToken = accessTokenResolver(request);

        JwtStatus jwtStatus;

        jwtStatus = StringUtils.hasText(accessToken) ? jwtUtils.validateToken(accessToken) : JwtStatus.EMPTY_TOKEN;

        handleJwtStatus(request, accessToken, jwtStatus);

        filterChain.doFilter(request, response);
    }

    /**
     * JWT Status 에 따라서 인증 처리
     */
    private void handleJwtStatus(HttpServletRequest request, String accessToken, JwtStatus jwtStatus) {
        switch (jwtStatus) {
            case ACCESS:
                String changeableId = jwtUtils.extractChangeableIdInToken(accessToken);
                if (loginServiceImpl.setAuthenticationInContextHolderByChangeableId(changeableId)) {
                    log.info("Access Token 정상 처리");
                    break;
                } else {
                    log.info("[JwtAuthenticationFilter is Logout token]");
                    jwtStatus = JwtStatus.LOGOUT;
                }
                break;
            case EXPIRED:
                log.info("[JwtAuthenticationFilter is EXPIRED token]");
                jwtStatus = JwtStatus.EXPIRED;
                break;
            case DENIED:
                log.info("[JwtAuthenticationFilter is DENIED token]");
                jwtStatus = JwtStatus.DENIED;
                break;
            case EMPTY_TOKEN:
                log.info("[JwtAuthenticationFilter is EMPTY token]");
                jwtStatus = JwtStatus.EMPTY_TOKEN;
                break;
            default:
                log.info("[JwtAuthenticationFilter is ERROR token]");
                jwtStatus = JwtStatus.ERROR;
        }
        request.setAttribute(JWT_STATUS_HEADER, jwtStatus);
    }


}


