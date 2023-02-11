package com.study.badrequest.utils.jwt;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.commons.exception.custom_exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_PREFIX;
import static com.study.badrequest.commons.consts.JwtTokenHeader.TOKEN_PREFIX;

@Component
@Slf4j
public class JwtUtils implements InitializingBean {

    private final static String AUTHORITIES_KEY = "auth";
    private final String SECRETE_KEY;
    private final int ACCESS_TOKEN_LIFE_MIN;
    private final int REFRESH_TOKEN_LIFE;

    private Key key;

    public JwtUtils(@Value("${token.secret-key}") String SECRETE_KEY,
                    @Value("${token.access-life}") int ACCESS_TOKEN_LIFE_MIN,
                    @Value("${token.refresh-life}") int REFRESH_TOKEN_LIFE_DAY) {
        this.SECRETE_KEY = SECRETE_KEY;
        this.ACCESS_TOKEN_LIFE_MIN = ACCESS_TOKEN_LIFE_MIN;
        this.REFRESH_TOKEN_LIFE = REFRESH_TOKEN_LIFE_DAY;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("[JwtUtils.afterPropertiesSet]");
        key = Keys.hmacShaKeyFor(Base64.getEncoder().encodeToString(SECRETE_KEY.getBytes()).getBytes());
    }

    /**
     * 토큰 생성
     */
    public TokenDto generateToken(Authentication authentication) {

        String authorityString = getAuthorityString(authentication);
        long currentTimeMillis = System.currentTimeMillis();

        log.info("[엑세스 토큰생성]");
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorityString)
                .setExpiration(new Date(currentTimeMillis + TimeUnit.MINUTES.toMillis(ACCESS_TOKEN_LIFE_MIN)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        log.info("[리프레쉬 토큰생성]");
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorityString)
                .setExpiration(new Date(currentTimeMillis + TimeUnit.DAYS.toMillis(REFRESH_TOKEN_LIFE)))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(getExpirationDateTime(accessToken))
                .refreshTokenExpiredTime(getExpirationTimeMillis(refreshToken))
                .build();
    }

    private static String getAuthorityString(Authentication authentication) {
        String collect = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return collect;
    }

    // 토큰의 유효성 + 만료일자 확인
    public JwtStatus validateToken(String token) {

        try {
            getClaimsJws(token);
            log.info("[JWT_UTILS validateToken= {}]", JwtStatus.ACCESS);
            return JwtStatus.ACCESS;
        } catch (ExpiredJwtException e) {
            log.info("[JWT_UTILS validateToken= {}]", JwtStatus.EXPIRED);
            return JwtStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("[JWT_UTILS validateToken= {}]", JwtStatus.DENIED);
            return JwtStatus.DENIED;
        } catch (Exception e) {
            return JwtStatus.ERROR;
        }
    }

    // 헤더에서 토큰 확인
    public String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            log.info("[JWT_UTILS resolveToken ={}]", bearerToken);
            return bearerToken.substring(7);
        }

        return null;
    }

    public String resolveTokenInRefreshCookie(Cookie cookie) {

        if (cookie != null && cookie.getValue() != null && cookie.getValue().startsWith(REFRESH_TOKEN_PREFIX)) {
            log.info("[JWT_UTILS resolveRefreshCookie ={}]", cookie.getValue());
            return cookie.getValue().substring(7);
        }
        return null;
    }

    public LocalDateTime getExpirationDateTime(String token) {
        return getClaimsJws(token)
                .getBody()
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public long getExpirationTimeMillis(String token) {
        LocalDateTime expirationDate = getExpirationDateTime(token);
        LocalDateTime currentDate = LocalDateTime.now();
        return Duration.between(currentDate, expirationDate).toMillis();
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        log.info("[JwtUtils. getAuthentication]");
        // 토큰 복호화
        Claims claims = getClaimsJws(accessToken).getBody();

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new TokenException(CustomStatus.TOKEN_IS_DENIED);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        User user = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    public void checkTokenIsEmpty(String token, CustomStatus status) {
        if (token == null) {
            throw new TokenException(status);
        }
    }
}
