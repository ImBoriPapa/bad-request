package com.study.badrequest.filter;

import com.study.badrequest.login.domain.service.JwtLoginService;
import com.study.badrequest.utils.JwtStatus;
import com.study.badrequest.utils.JwtUtils;
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

    // TODO: 2023/01/02 logout
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[JwtAuthenticationFilter]");
        String accessToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        if (accessToken == null) {
            request.setAttribute(JWT_STATUS_HEADER, JwtStatus.EMPTY_TOKEN);
        }

        if (StringUtils.hasText(accessToken)) {

            JwtStatus jwtStatus = jwtUtils.validateToken(accessToken);

            switch (jwtStatus) {
                case ACCESS:
                    Authentication authentication = jwtUtils.getAuthentication(accessToken);

                    if (loginService.loginCheck(authentication.getName())) {
                        log.info("[JwtAuthenticationFilter .Set SecurityContextHolder Context]");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else
                        log.info("[JwtAuthenticationFilter .is Logout token]");
                    request.setAttribute(JWT_STATUS_HEADER, JwtStatus.LOGOUT);
                    break;
                case EXPIRED:
                    request.setAttribute(JWT_STATUS_HEADER, JwtStatus.EXPIRED);
                    break;
                case DENIED:
                    request.setAttribute(JWT_STATUS_HEADER, JwtStatus.DENIED);
                    break;
            }
        }

        filterChain.doFilter(request, response);
    }
}
