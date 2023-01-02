package com.study.badrequest.filter;

import com.study.badrequest.login.domain.entity.RefreshToken;
import com.study.badrequest.login.domain.repository.RefreshTokenRepository;

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
import java.util.Optional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.AUTHORIZATION_HEADER;
import static com.study.badrequest.commons.consts.JwtTokenHeader.JWT_STATUS_HEADER;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    // TODO: 2023/01/02 logout
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtils.resolveToken(request, AUTHORIZATION_HEADER);

        if(accessToken == null){
            request.setAttribute(JWT_STATUS_HEADER, JwtStatus.EMPTY_TOKEN);
        }

        if (StringUtils.hasText(accessToken)) {
            log.info("[JwtAuthenticationFilter accessToken= {}]", accessToken);

            JwtStatus jwtStatus = jwtUtils.validateToken(accessToken);

            switch (jwtStatus) {
                case ACCESS:
                    Optional<RefreshToken> refresh = refreshTokenRepository.findById(1L);
                    if (refresh.isPresent()) {
                        Authentication authentication = jwtUtils.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
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
