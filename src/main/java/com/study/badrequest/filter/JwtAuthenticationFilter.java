package com.study.badrequest.filter;

import com.study.badrequest.member.command.application.LoginService;
import com.study.badrequest.common.status.JwtStatus;
import com.study.badrequest.member.command.domain.values.MemberJwtDecodedPayload;
import com.study.badrequest.member.command.domain.values.MemberJwtEncodedPayload;
import com.study.badrequest.utils.jwt.JwtPayloadEncoder;
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

import static com.study.badrequest.common.constants.AuthenticationHeaders.JWT_STATUS_HEADER;
import static com.study.badrequest.utils.authentication.AuthenticationFactory.generateAuthentication;
import static com.study.badrequest.utils.header.HttpHeaderResolver.accessTokenResolver;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final LoginService loginServiceImpl;

    // TODO: 2023/07/30 Add Test
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request URI= {}", request.getRequestURI());

        String accessToken = accessTokenResolver(request);

        JwtStatus jwtStatus;

        jwtStatus = StringUtils.hasText(accessToken) ? jwtUtils.validateToken(accessToken) : JwtStatus.EMPTY_TOKEN;

        handleJwtStatus(request, accessToken, jwtStatus);

        filterChain.doFilter(request, response);
    }

    private void handleJwtStatus(HttpServletRequest request, String accessToken, JwtStatus jwtStatus) {
        switch (jwtStatus) {
            case ACCESS:

                MemberJwtEncodedPayload encodedPayload = jwtUtils.getAccessTokenPayload(accessToken);
                MemberJwtDecodedPayload decodedPayload = new JwtPayloadEncoder().decodedPayload(encodedPayload);

                Authentication authentication = generateAuthentication(decodedPayload.getMemberId(), decodedPayload.getAuthority());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("Access token processed successfully");

                break;
            case EXPIRED:
                log.info("[Access token status is EXPIRED token]");
                jwtStatus = JwtStatus.EXPIRED;
                break;
            case DENIED:
                log.info("[Access token status is DENIED token]");
                jwtStatus = JwtStatus.DENIED;
                break;
            case EMPTY_TOKEN:
                log.info("[Access token status is EMPTY token]");
                jwtStatus = JwtStatus.EMPTY_TOKEN;
                break;
            default:
                log.info("[Access token status is ERROR token]");
                jwtStatus = JwtStatus.ERROR;
        }
        request.setAttribute(JWT_STATUS_HEADER, jwtStatus);
    }


}


