package com.now.naaga.auth.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.auth.infrastructure.jwt.JwtProvider;
import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
import org.springframework.stereotype.Component;

import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_HEADER;
import static com.now.naaga.auth.exception.AuthExceptionType.NOT_EXIST_HEADER;

@Component
public class BearerAuthExtractor implements AuthenticationExtractor<MemberAuthRequest> {

    private static final String BEARER_TYPE = "bearer";

    private final JwtProvider jwtProvider;

    public BearerAuthExtractor(final JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public MemberAuthRequest extract(final String header) {
        if (header == null) {
            throw new AuthException(NOT_EXIST_HEADER);
        }
        if (!header.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            throw new AuthException(INVALID_HEADER);
        }
        final String accessToken = header.substring(BEARER_TYPE.length()).trim();

        final String subject = jwtProvider.extractSubject(accessToken);
        final MemberAuth memberAuth = MemberAuthMapper.convertStringToMemberAuth(subject);

        return new MemberAuthRequest(memberAuth);
    }
}
