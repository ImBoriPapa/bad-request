package com.study.badrequest.filter;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.utils.jwt.JwtAuthenticationFilterResponseFactory;
import com.study.badrequest.utils.jwt.JwtStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;

import static com.study.badrequest.commons.constants.JwtTokenHeader.JWT_STATUS_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPointFilter implements AuthenticationEntryPoint {

    private final JwtAuthenticationFilterResponseFactory jwtAuthenticationFilterResponseFactory;
    private final EnumMap<JwtStatus, ApiResponseStatus> jwtStatusEnumMap = new EnumMap<>(JwtStatus.class);

    {
        jwtStatusEnumMap.put(JwtStatus.EMPTY_TOKEN, ApiResponseStatus.TOKEN_IS_EMPTY);
        jwtStatusEnumMap.put(JwtStatus.EXPIRED, ApiResponseStatus.TOKEN_IS_EXPIRED);
        jwtStatusEnumMap.put(JwtStatus.DENIED, ApiResponseStatus.TOKEN_IS_DENIED);
        jwtStatusEnumMap.put(JwtStatus.LOGOUT, ApiResponseStatus.ALREADY_LOGOUT);
        jwtStatusEnumMap.put(JwtStatus.ERROR, ApiResponseStatus.TOKEN_IS_ERROR);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("[JwtAuthenticationEntryPointFilter]");

        JwtStatus jwtStatus = (JwtStatus) request.getAttribute(JWT_STATUS_HEADER);

        ApiResponseStatus apiResponseStatus = jwtStatusEnumMap.getOrDefault(jwtStatus, ApiResponseStatus.TOKEN_IS_ERROR);

        jwtAuthenticationFilterResponseFactory.setErrorResponse(request, response, apiResponseStatus);



    }


}
