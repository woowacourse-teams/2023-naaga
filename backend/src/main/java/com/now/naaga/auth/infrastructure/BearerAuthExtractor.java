package com.now.naaga.auth.infrastructure;

import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.common.exception.InternalException;
import org.springframework.stereotype.Component;

import static com.now.naaga.auth.exception.AuthExceptionType.*;

@Component
public class BearerAuthExtractor implements AuthenticationExtractor<MemberAuth> {

    private static final String BEARER_TYPE = "bearer";

    private final JwtProvider jwtProvider;

    public BearerAuthExtractor(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public MemberAuth extract(final String header) {
        if (header == null) {
            throw new AuthException(NOT_EXIST_HEADER);
        }
        if (!header.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            throw new AuthException(INVALID_HEADER);
        }
        final String accessToken = header.substring(BEARER_TYPE.length()).trim();

        final String subject = jwtProvider.extractSubject(accessToken);
        try {
            return MemberAuthMapper.convertStringToMemberAuth(subject);
        } catch (InternalException e) {
            throw new AuthException(INVALID_TOKEN);
        }
    }
}
