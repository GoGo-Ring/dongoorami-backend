package com.gogoring.dongoorami.global.jwt;

import com.gogoring.dongoorami.member.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final CustomUserDetailsService customUserDetailsService;

    private final TokenRepository tokenRepository;

    private final String secret;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;
    private Key key;

    public TokenProvider(CustomUserDetailsService customUserDetailsService,
            TokenRepository tokenRepository,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration-time}") Long accessExpirationTime,
            @Value("${jwt.refresh-expiration-time}") Long refreshExpirationTime) {
        this.customUserDetailsService = customUserDetailsService;
        this.tokenRepository = tokenRepository;
        this.secret = secret;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String providerId, List<GrantedAuthority> grantedAuthorities) {

        String authorities = grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Claims claims = Jwts.claims()
                .setSubject(providerId);
        claims.put("authorities", authorities);

        Date expirationTime = getExpirationTime(accessExpirationTime);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TOKEN_PREFIX + accessToken;
    }

    public String createRefreshToken(String providerId) {

        Claims claims = Jwts.claims()
                .setSubject(providerId);

        Date expirationTime = getExpirationTime(refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        Duration duration = Duration.between(Instant.now(), expirationTime.toInstant());
        tokenRepository.save(providerId, refreshToken, duration);

        return refreshToken;
    }

    public Authentication getAuthentication(String token) {

        String providerId = parseClaims(token).getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(providerId);

        return new UsernamePasswordAuthenticationToken(userDetails, token,
                userDetails.getAuthorities());
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Claims claims = parseClaims(accessToken);
            String signOutToken = tokenRepository.findByKey(accessToken);

            return !claims.getExpiration().before(new Date()) && (signOutToken == null);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT입니다.", e);
        }

        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = parseClaims(refreshToken);

            String providerId = claims.getSubject();
            String savedRefreshToken = tokenRepository.findByKey(providerId);

            return refreshToken.equals(savedRefreshToken);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT입니다.", e);
        }

        return false;
    }

    public String getProviderId(String token) {
        return parseClaims(token).getSubject();
    }

    public Duration getRestExpirationTime(String token) {
        return Duration.between(Instant.now(), parseClaims(token).getExpiration().toInstant());
    }

    public String getTokenWithNoPrefix(String accessToken) {
        return accessToken.replace(TOKEN_PREFIX, "");
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date getExpirationTime(Long expirationTime) {
        return new Date((new Date()).getTime() + expirationTime);
    }
}
