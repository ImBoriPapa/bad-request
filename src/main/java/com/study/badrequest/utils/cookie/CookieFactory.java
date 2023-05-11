package com.study.badrequest.utils.cookie;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import static com.study.badrequest.commons.constants.JwtTokenHeader.REFRESH_TOKEN_COOKIE;
import static com.study.badrequest.commons.constants.JwtTokenHeader.REFRESH_TOKEN_PREFIX;
@Slf4j
public class CookieFactory {
    private static boolean secure;
    @Value("${cookie-status.secure}")
    public void setSecure(boolean secure) {
        CookieFactory.secure = secure;
    }

    public static ResponseCookie createRefreshTokenCookie(String refreshToken, long expiration) {

        return ResponseCookie.from(REFRESH_TOKEN_COOKIE,refreshToken)
                .maxAge(Duration.ofMillis(expiration))
                .path("/")
                .secure(secure)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setPath("/");
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> tClass) {
        return tClass.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}