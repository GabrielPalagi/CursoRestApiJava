package com.palagi.demo_park_api.jwt;

import com.palagi.demo_park_api.config.EnvConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {

    public static final String JWT_BEARER = EnvConfig.getString("JWT_BEARER");//"Bearer ";

    public static final String JWT_AUTHORIZATION = EnvConfig.getString("JWT_AUTHORIZATION");//"Authorization";

    public static final String SECRET_KEY = EnvConfig.getString("SECRET_KEY");

    public static final long EXPIRE_DAYS = EnvConfig.getLong("EXPIRE_DAYS");

    public static final long EXPIRE_HOURS = EnvConfig.getLong("EXPIRE_HOURS");

    public static final long EXPIRE_MINUTES = EnvConfig.getLong("EXPIRE_MINUTES");

    private JwtUtils() {
    }

    private static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static JwtToken createToken(String username, String role) {
        Date issuedAt = new Date();
        Date Limit = toExpireDate(issuedAt);

        String token = Jwts.builder().setHeaderParam("typ", "JWT").setSubject(username).setIssuedAt(issuedAt).setExpiration(Limit).signWith(generateKey(), SignatureAlgorithm.HS256).claim("role", role).compact();

        return new JwtToken(token);
    }

    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(refactorToken(token)).getBody();

        } catch (JwtException ex) {
            log.error(String.format("Token invalido %s", ex.getMessage()));
        }
        return null;
    }

    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(refactorToken(token));

            return true;

        } catch (JwtException ex) {
            log.error(String.format("Token invalido %s", ex.getMessage()));
        }
        return false;
    }

    private static String refactorToken(String token) {
        if (token.contains(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length());
        }
        return token;

    }
}
