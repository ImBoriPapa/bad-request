package com.study.badrequest.utils.jwt;

import com.study.badrequest.common.status.JwtStatus;
import com.study.badrequest.dto.jwt.JwtTokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Component
@Slf4j
public class JwtUtils {
    private final String SECRETE_KEY;
    private final int ACCESS_TOKEN_LIFETIME_MINUTES;
    private final int REFRESH_TOKEN_LIFE_DAYS;
    private final Key key;

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
     * 2023/05/12
     * Username 으로 TokenDto 생성 후 반환 -> username -> changeableId
     */
    public JwtTokenDto generateJwtTokens(String changeableId) {

        log.info("[GENERATE ACCESS TOKEN]");
        String accessToken = createToken(changeableId, MINUTES, ACCESS_TOKEN_LIFETIME_MINUTES);

        log.info("[GENERATE REFRESH TOKEN]");
        String refreshToken = createToken(changeableId, DAYS, REFRESH_TOKEN_LIFE_DAYS);

        return JwtTokenDto.builder()
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
     * 토큰에 저장된 토큰 만료 시간*
     *
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
     * 토큰 바디에 저장된 ChangeableId 반환
     *
     * @param token
     * @return String ChangeableId
     */
    public String extractChangeableIdInToken(String token) {
        return getClaimsJws(token).getBody().getSubject();
    }


}
