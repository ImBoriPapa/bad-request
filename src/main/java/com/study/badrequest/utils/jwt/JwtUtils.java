package com.study.badrequest.utils.jwt;

import com.study.badrequest.commons.consts.CustomStatus;
import com.study.badrequest.exception.custom_exception.TokenException;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.study.badrequest.commons.consts.JwtTokenHeader.REFRESH_TOKEN_PREFIX;
import static com.study.badrequest.commons.consts.JwtTokenHeader.TOKEN_PREFIX;

@Component
@Slf4j
public class JwtUtils implements InitializingBean {

    private final static String AUTHORITIES_KEY = "auth";
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
     */
    public TokenDto generateToken(Authentication authentication) {

        String collect = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        long now = new Date().getTime();

        log.info("[엑세스 토큰생성]");
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, collect)
                .setExpiration(new Date(now + ACCESS_TOKEN_LIFE))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        log.info("[리프레쉬 토큰생성]");
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, collect)
                .setExpiration(new Date(now + REFRESH_TOKEN_LIFE))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(getExpirationDate(accessToken))
                .refreshTokenExpiredTime(getExpirationTime(refreshToken))
                .build();
    }

    // 토큰에서 회원 정보 추출
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

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

    public String resolveRefreshCookie(Cookie cookie) {

        if (cookie != null && cookie.getValue() != null && cookie.getValue().startsWith(REFRESH_TOKEN_PREFIX)) {
            log.info("[JWT_UTILS resolveRefreshCookie ={}]", cookie.getValue());
            return cookie.getValue().substring(7);
        }
        return null;
    }

    public Date getExpirationDate(String token) {
        return getClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public long getExpirationTime(String token) {
        long expiration = getExpirationDate(token).getTime();
        return (expiration - new Date().getTime());
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
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


}
