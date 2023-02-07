package com.study.badrequest.filter;

import com.study.badrequest.commons.consts.CustomStatus;
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

import static com.study.badrequest.commons.consts.JwtTokenHeader.JWT_STATUS_HEADER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPointFilter implements AuthenticationEntryPoint {


    private final JwtAuthenticationFilterResponseFactory jwtAuthenticationFilterResponseFactory;
    private final EnumMap<JwtStatus, CustomStatus> jwtStatusEnumMap = new EnumMap<>(JwtStatus.class);

    {
        jwtStatusEnumMap.put(JwtStatus.EMPTY_TOKEN, CustomStatus.TOKEN_IS_EMPTY);
        jwtStatusEnumMap.put(JwtStatus.EXPIRED, CustomStatus.TOKEN_IS_EXPIRED);
        jwtStatusEnumMap.put(JwtStatus.DENIED, CustomStatus.TOKEN_IS_DENIED);
        jwtStatusEnumMap.put(JwtStatus.LOGOUT, CustomStatus.ALREADY_LOGOUT);
        jwtStatusEnumMap.put(JwtStatus.ERROR, CustomStatus.TOKEN_IS_ERROR);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("[JwtAuthenticationEntryPointFilter]");
        JwtStatus jwtStatus = (JwtStatus) request.getAttribute(JWT_STATUS_HEADER);

        CustomStatus customStatus = jwtStatusEnumMap.getOrDefault(jwtStatus, CustomStatus.TOKEN_IS_ERROR);

        jwtAuthenticationFilterResponseFactory.setErrorResponse(request, response, customStatus);
    }


}
