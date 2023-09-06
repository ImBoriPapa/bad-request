package com.study.badrequest.utils.jwt;

import com.study.badrequest.common.status.JwtStatus;
import com.study.badrequest.login.command.interfaces.JwtTokenDto;
import com.study.badrequest.member.command.domain.values.MemberJwtEncodedPayload;
import com.study.badrequest.member.query.dto.MemberLoginInformation;
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

@Component
@Slf4j
public class JwtUtils {
    private final String SECRETE_KEY;
    private final int ACCESS_TOKEN_LIFETIME_MINUTES;
    private final int REFRESH_TOKEN_LIFE_DAYS;
    private final Key key;

    public JwtUtils(@Value("${token.secret-key}") String secreteKey,
                    @Value("${token.access-life}") int accessTokenLifetimeMinutes,
                    @Value("${token.refresh-life}") int refreshTokenLifeDay) {
        this.SECRETE_KEY = secreteKey;
        this.ACCESS_TOKEN_LIFETIME_MINUTES = accessTokenLifetimeMinutes;
        this.REFRESH_TOKEN_LIFE_DAYS = refreshTokenLifeDay;
        this.key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(secreteKey.getBytes()).getBytes());
    }

    /**
     * 토큰 생성 로직
     * 2023/03/07
     * - 토큰에 권한정보(Authority) 제거
     * 2023/05/12
     * Username 으로 TokenDto 생성 후 반환 -> username -> changeableId
     */
    public JwtTokenDto generateJwtTokens(MemberJwtEncodedPayload memberJwtEncodedPayload) {

        final String accessToken = createAccessToken(memberJwtEncodedPayload, ACCESS_TOKEN_LIFETIME_MINUTES);

        final String refreshToken = createRefreshToken(memberJwtEncodedPayload.getMemberId(), REFRESH_TOKEN_LIFE_DAYS);

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(getExpirationLocalDateTime(accessToken))
                .refreshTokenExpirationMill(getExpirationTimeMillis(refreshToken))
                .build();
    }


    private String createAccessToken(MemberJwtEncodedPayload memberJwtEncodedPayload, int lifeTime) {
        return Jwts.builder()
                .claim("memberId", memberJwtEncodedPayload.getMemberId())
                .claim("authority", memberJwtEncodedPayload.getAuthority())
                .claim("status", memberJwtEncodedPayload.getStatus())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(lifeTime)))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }

    private String createRefreshToken(String memberId, int lifeTime) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(lifeTime)))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }


    /**
     * 토큰 검증 로직
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
     * 토큰에 저장된 토큰 만료 시간
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
     */
    public long getExpirationTimeMillis(String token) {
        LocalDateTime expirationDate = getExpirationLocalDateTime(token);
        LocalDateTime currentDate = LocalDateTime.now();
        return Duration.between(currentDate, expirationDate).toMillis();
    }

    public MemberJwtEncodedPayload getAccessTokenPayload(String token) {
        Claims claims = getClaimsJws(token).getBody();
        String memberId = claims.get("memberId", String.class);
        String authority = claims.get("authority", String.class);
        String status = claims.get("status", String.class);
        return new MemberJwtEncodedPayload(memberId, authority, status);
    }


}
