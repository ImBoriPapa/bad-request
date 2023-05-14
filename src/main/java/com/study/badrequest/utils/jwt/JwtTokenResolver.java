package com.study.badrequest.utils.jwt;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import static com.study.badrequest.commons.constants.JwtTokenHeader.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.commons.constants.JwtTokenHeader.AUTHORIZATION_HEADER;

@Slf4j
public class JwtTokenResolver {

    public static String accessTokenResolver(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            log.info("Resolve AccessToken Token ={}", bearerToken);
            return bearerToken.substring(7);
        }
        log.info("Resolve AccessToken Token = NULL");
        return null;
    }

}
