package com.study.badrequest.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static com.study.badrequest.commons.consts.JwtTokenHeader.TOKEN_PREFIX;

@Component
@Slf4j
public class JwtUtils implements InitializingBean {

    private final String SECRETE_KEY;
    private final long ACCESS_TOKEN_LIFE;
    private final long REFRESH_TOKEN_LIFE;
    private Key key;

    public JwtUtils(@Value("${token.secret-key}") String SECRETE_KEY,
                    @Value("${token.access-life}") long ACCESS_TOKEN_LIFE,
                    @Value("${token.refresh-life}") long REFRESH_TOKEN_LIFE) {
        this.SECRETE_KEY = SECRETE_KEY;
        this.ACCESS_TOKEN_LIFE = ACCESS_TOKEN_LIFE * 1000;
        this.REFRESH_TOKEN_LIFE = REFRESH_TOKEN_LIFE * 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("[JwtUtils.afterPropertiesSet]");
        key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(SECRETE_KEY.getBytes()).getBytes());
    }

    /**
     * 토큰 생성
     * @param memberId
     * setSubject : 정보 저장
     * setIssuedAt() : 토큰 발행 시간 정보
     * signWith() : 암호화
     */
    public String generateAccessToken(String memberId) {
        log.info("[엑세스 토큰생성]");
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_LIFE);
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    //리프레시 토큰 생성
    public String generateRefreshToken(String memberId) {
        log.info("[리프레쉬 토큰생성]");
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_LIFE);
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiration)
                .compact();
    }

    // 토큰에서 회원 정보 추출
    public String getMemberId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();

    }


    // 토큰의 유효성 + 만료일자 확인
    public JwtCode validateToken(String token) {
        log.info("==============[JWT_UTILS] 토큰 검증  =============");
        try {
            getClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            return JwtCode.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return JwtCode.DENIED;
        }
    }

    // 헤더에서 토큰 확인
    public Optional<String> resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if (StringUtils.hasLength(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return Optional.ofNullable(bearerToken.substring(7));
        }
        return Optional.empty();
    }


    public Date getExpired(String token) {
        Jws<Claims> claimsJws = getClaimsJws(token);
        return claimsJws.getBody().getExpiration();
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

}
