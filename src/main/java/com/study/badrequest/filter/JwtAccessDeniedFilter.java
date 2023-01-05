package com.study.badrequest.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.form.ResponseForm;
import com.study.badrequest.exception.JwtAuthenticationException;
import com.study.badrequest.login.api.LoginController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("[JwtAccessDeniedFilter]");
        setFilterResponse(request, response, CustomStatus.PERMISSION_DENIED);

    }

    public void setFilterResponse(HttpServletRequest request, HttpServletResponse response, CustomStatus status) throws IOException {
        log.info("[JwtAccessDeniedFilter. response error]");
        ResponseForm.Error error = new ResponseForm.Error(new JwtAuthenticationException(status), request.getRequestURI());
        // TODO: 2023/01/05 권한 접근시 URL 추가 고민
        //        EntityModel<ResponseForm.Error> model = EntityModel.of(error);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
