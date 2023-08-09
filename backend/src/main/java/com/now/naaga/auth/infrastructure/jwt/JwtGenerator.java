package com.now.naaga.auth.infrastructure.jwt;

import com.now.naaga.auth.damain.AuthTokens;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtGenerator {

    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 2;  // 2일
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final JwtProvider jwtProvider;

    public JwtGenerator(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public AuthTokens generate(final Long memberId) {
        final long now = (new Date()).getTime();
        final Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        final Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        final String subject = memberId.toString();
        final String accessToken = jwtProvider.generate(subject, accessTokenExpiredAt);
        final String refreshToken = jwtProvider.generate(subject, refreshTokenExpiredAt);

        return new AuthTokens(accessToken, refreshToken);
    }
}
