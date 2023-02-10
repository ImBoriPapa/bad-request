package com.study.badrequest.utils.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.commons.exception.custom_exception.JwtAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilterResponseFactory {

    private final ObjectMapper objectMapper;

    public void setErrorResponse(HttpServletRequest request, HttpServletResponse response, CustomStatus status) throws IOException {
        log.info("[JwtAuthenticationFilterResponseFactory. response error status={}]", status);
        setResponseHeader(response);
        response.getWriter().write(objectMapper.writeValueAsString(getErrorResponse(request, status)));
    }

    private void setResponseHeader(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }

    private ResponseForm.Error getErrorResponse(HttpServletRequest request, CustomStatus status) {
        return new ResponseForm.Error(new JwtAuthenticationException(status), request.getRequestURI());

    }

}
