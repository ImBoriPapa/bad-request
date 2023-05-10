package com.study.badrequest.utils.jwt;

import com.study.badrequest.commons.response.ApiResponseStatus;
import com.study.badrequest.exception.custom_exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.study.badrequest.commons.constants.JwtTokenHeader.REFRESH_TOKEN_PREFIX;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Component
@Slf4j
public class JwtUtils {
    private final String SECRETE_KEY;
    private final int ACCESS_TOKEN_LIFETIME_MINUTES;
    private final int REFRESH_TOKEN_LIFE_DAYS;
    private Key key;

    /**
     * 인스턴스 생성시 변수 초기화
     *
     * @param SECRETE_KEY
     * @param ACCESS_TOKEN_LIFETIME_MINUTES
     * @param REFRESH_TOKEN_LIFE_DAY
     */
    public JwtUtils(@Value("${token.secret-key}") String SECRETE_KEY,
                    @Value("${token.access-life}") int ACCESS_TOKEN_LIFETIME_MINUTES,
                    @Value("${token.refresh-life}") int REFRESH_TOKEN_LIFE_DAY) {
        this.SECRETE_KEY = SECRETE_KEY;
        this.ACCESS_TOKEN_LIFETIME_MINUTES = ACCESS_TOKEN_LIFETIME_MINUTES;
        this.REFRESH_TOKEN_LIFE_DAYS = REFRESH_TOKEN_LIFE_DAY;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(SECRETE_KEY.getBytes()).getBytes());
    }

    /**
     * 토큰 생성 로직
     * 2023/03/07
     * - 토큰에 권한정보(Authority) 제거
     * - 상수명 변경: ACCESS_TOKEN_LIFETIME_MIN -> ACCESS_TOKEN_LIFETIME_MINUTES, REFRESH_TOKEN_LIFE -> REFRESH_TOKEN_LIFE_DAYS
     * <p>
     * Username 으로 TokenDto 생성 후 반환
     */
    public TokenDto generateJwtTokens(String username) {

        log.info("[GENERATE ACCESS TOKEN]");
        String accessToken = createToken(username, MINUTES, ACCESS_TOKEN_LIFETIME_MINUTES);

        log.info("[GENERATE REFRESH TOKEN]");
        String refreshToken = createToken(username, DAYS, REFRESH_TOKEN_LIFE_DAYS);

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(getExpirationLocalDateTime(accessToken))
                .refreshTokenExpirationMill(getExpirationTimeMillis(refreshToken))
                .build();
    }

    /**
     * 토큰 생성 로직
     *
     * @param username
     * @param timeUnit
     * @param lifeTime
     * @return String token
     */
    private String createToken(String username, TimeUnit timeUnit, int lifeTime) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + timeUnit.toMillis(lifeTime)))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 토큰 검증 로직
     *
     * @param token
     * @return JwtStatus
     */
    public JwtStatus validateToken(String token) {

        try {
            getClaimsJws(token);
            log.info("[TOKEN VERIFICATION RESULT= {}]", JwtStatus.ACCESS);
            return JwtStatus.ACCESS;
        } catch (ExpiredJwtException e) {
            log.info("[TOKEN VERIFICATION RESULT= {}]", JwtStatus.EXPIRED);
            return JwtStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("[TOKEN VERIFICATION RESULT= {}]", JwtStatus.DENIED);
            return JwtStatus.DENIED;
        } catch (Exception e) {
            return JwtStatus.ERROR;
        }
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * 쿠키에 저장된 토큰 확인
     */
    public String resolveTokenInRefreshCookie(Cookie cookie) {
        if (cookie != null && cookie.getValue() != null && cookie.getValue().startsWith(REFRESH_TOKEN_PREFIX)) {
            log.info("[JWT_UTILS resolveRefreshCookie ={}]", cookie.getValue());
            return cookie.getValue().substring(7);
        }
        return null;
    }

    /**
     * 토큰에 저장된 토큰 만료 시간*
     * @Return LocalDateTime
     */
    public LocalDateTime getExpirationLocalDateTime(String token) {
        return getClaimsJws(token)
                .getBody()
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Refresh 토큰 만료까지 남은 시간
     *
     * @return long
     */
    public long getExpirationTimeMillis(String token) {
        LocalDateTime expirationDate = getExpirationLocalDateTime(token);
        LocalDateTime currentDate = LocalDateTime.now();
        return Duration.between(currentDate, expirationDate).toMillis();
    }


    /**
     * 토큰 바디에 저장된 Username 반환
     *
     * @param token
     * @return username
     */
    public String getUsernameInToken(String token) {
        return getClaimsJws(token).getBody().getSubject();
    }
    public void checkTokenIsEmpty(String token, ApiResponseStatus status) {
        if (token == null) {
            throw new TokenException(status);
        }
    }
}
