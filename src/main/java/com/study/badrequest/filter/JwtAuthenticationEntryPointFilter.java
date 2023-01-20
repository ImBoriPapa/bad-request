package com.study.badrequest.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.exception.custom_exception.JwtAuthenticationException;
import com.study.badrequest.utils.jwt.JwtStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.study.badrequest.commons.consts.JwtTokenHeader.JWT_STATUS_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPointFilter implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("[JwtAuthenticationEntryPointFilter]");
        JwtStatus jwtStatus = (JwtStatus) request.getAttribute(JWT_STATUS_HEADER);

        if (jwtStatus == JwtStatus.EMPTY_TOKEN) {
            setFilterResponse(request, response, CustomStatus.TOKEN_IS_EMPTY);
        }

        if (jwtStatus == JwtStatus.EXPIRED) {
            setFilterResponse(request, response, CustomStatus.TOKEN_IS_EXPIRED);
        }

        if (jwtStatus == JwtStatus.DENIED) {
            setFilterResponse(request, response, CustomStatus.TOKEN_IS_DENIED);
        }

        if (jwtStatus == JwtStatus.LOGOUT) {
            setFilterResponse(request, response, CustomStatus.ALREADY_LOGOUT);
        }

        if (jwtStatus == JwtStatus.ERROR) {
            setFilterResponse(request, response, CustomStatus.TOKEN_IS_DENIED);
        }
    }

    private void setFilterResponse(HttpServletRequest request, HttpServletResponse response, CustomStatus status) throws IOException {
        log.info("[JwtAuthenticationEntryPointFilter. response error status={}]",status);
        ResponseForm.Error error = new ResponseForm.Error(new JwtAuthenticationException(status), request.getRequestURI());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
