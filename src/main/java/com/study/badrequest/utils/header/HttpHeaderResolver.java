package com.study.badrequest.utils.header;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static com.study.badrequest.commons.constants.AuthenticationHeaders.ACCESS_TOKEN_PREFIX;
import static com.study.badrequest.commons.constants.AuthenticationHeaders.AUTHORIZATION_HEADER;
@Slf4j
public class HttpHeaderResolver {

    public static String accessTokenResolver(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(ACCESS_TOKEN_PREFIX)) {
            log.info("Resolve AccessToken Token ={}", bearerToken);
            return bearerToken.substring(7);
        }
        log.info("Resolve AccessToken Token = NULL");
        return null;
    }

    public static String ipAddressResolver(HttpServletRequest request) {

        String remoteAddr = request.getRemoteAddr();

        String ip = getIp(remoteAddr);

        if (ip == null) {
            return Arrays.stream(ClientIpHeader.values())
                    .filter(clientIpHeader -> getIp(request.getHeader(clientIpHeader.getHeaderName())) != null)
                    .findFirst()
                    .orElse(ClientIpHeader.UNKNOWN_CLIENT_IP)
                    .getHeaderName();
        }
        return ip;
    }

    private static String getIp(String remoteAddr) {
        return remoteAddr == null || remoteAddr.length() == 0 || "unknown".equalsIgnoreCase(remoteAddr) ? null : remoteAddr;
    }

    @Getter
    public enum ClientIpHeader {
        X_FORWARDED_FOR("X-Forwarded-For"),
        PROXY_CLIENT_IP("Proxy-Client-IP"),
        WL_PROXY_CLIENT_IP("WL-Proxy-Client-IP"),
        HTTP_CLIENT_IP("HTTP_CLIENT_IP"),
        HTTP_X_FORWARDED_FOR("HTTP_X_FORWARDED_FOR"),
        UNKNOWN_CLIENT_IP("UNKNOWN_CLIENT_IP");

        private final String headerName;

        ClientIpHeader(String headerName) {
            this.headerName = headerName;
        }
    }
}
