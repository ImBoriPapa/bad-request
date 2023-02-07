package com.study.badrequest.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.exception.custom_exception.JwtAuthenticationException;
import com.study.badrequest.utils.jwt.JwtAuthenticationFilterResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAccessDeniedFilter implements AccessDeniedHandler {

    private final JwtAuthenticationFilterResponseFactory jwtAuthenticationFilterResponseFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("[JwtAccessDeniedFilter]");
        jwtAuthenticationFilterResponseFactory.setErrorResponse(request, response, CustomStatus.PERMISSION_DENIED);

    }


}
