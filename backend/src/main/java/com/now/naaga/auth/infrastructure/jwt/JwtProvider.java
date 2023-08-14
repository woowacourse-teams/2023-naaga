package com.now.naaga.auth.infrastructure.jwt;

import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.auth.exception.AuthExceptionType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret-key}") final String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generate(final String subject, final Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractSubject(final String accessToken) {
        final Claims claims = parseClaims(accessToken);
        return claims.getSubject();
    }

    private Claims parseClaims(final String accessToken){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthExceptionType.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        }
    }
}

/*
액세스 토큰
클레임.
현재 시간

 */
