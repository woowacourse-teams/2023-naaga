package com.now.naaga.auth.infrastructure.jwt;

import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.MemberAuthMapper;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_TOKEN_ACCESS;

@Component
public class AuthTokenGenerator {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 100L * 365 * 24 * 60 * 60 * 1000;   // 100년
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 100L * 365 * 24 * 60 * 60 * 1000;  // 100년

    private final JwtProvider jwtProvider;

    public AuthTokenGenerator(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public AuthToken generate(final Member member,
                              final Long authId,
                              final AuthType authType) {
        final long now = (new Date()).getTime();
        final Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        final Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        final MemberAuth memberAuth = new MemberAuth(member.getId(), authId, authType);
        final String subject = MemberAuthMapper.convertMemberAuthToString(memberAuth);
        final String accessToken = jwtProvider.generate(subject, accessTokenExpiredAt);
        final String refreshToken = jwtProvider.generate(subject, refreshTokenExpiredAt);

        return new AuthToken(accessToken, refreshToken, member);
    }

    public AuthToken refresh(final AuthToken authToken) {
        final String subject = jwtProvider.extractSubject(authToken.getRefreshToken());
        final MemberAuth memberAuth = MemberAuthMapper.convertStringToMemberAuth(subject);

        final String accessToken = authToken.getAccessToken();
        if (jwtProvider.isNotExpired(accessToken)) {
            throw new AuthException(INVALID_TOKEN_ACCESS);
        }

        return generate(authToken.getMember(), memberAuth.getAuthId(), memberAuth.getAuthType());
    }
}
