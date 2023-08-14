package com.now.naaga.auth.infrastructure.jwt;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.exception.AuthException;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_TOKEN_ACCESS;

@Component
public class AuthTokenGenerator {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일

    private final JwtProvider jwtProvider;

    public AuthTokenGenerator(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public AuthToken generate(final Long memberId) {
        final long now = (new Date()).getTime();
        final Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        final Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        final String subject = memberId.toString(); // 1
        final String accessToken = jwtProvider.generate(subject, accessTokenExpiredAt);
        final String refreshToken = jwtProvider.generate(subject, refreshTokenExpiredAt);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken refresh(final AuthToken authToken) {
        final String subject = jwtProvider.extractSubject(authToken.getRefreshToken());
        final Long memberId = Long.valueOf(subject);

        final String accessToken = authToken.getAccessToken();
        if(jwtProvider.isNotExpired(accessToken)) {
            throw new AuthException(INVALID_TOKEN_ACCESS);
        }

        return generate(memberId);
    }
}
