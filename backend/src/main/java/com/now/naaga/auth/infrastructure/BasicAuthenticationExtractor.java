package com.now.naaga.auth.infrastructure;

import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
import org.springframework.stereotype.Component;

import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_HEADER;
import static com.now.naaga.auth.exception.AuthExceptionType.NOT_EXIST_HEADER;

@Component
public class BasicAuthenticationExtractor implements AuthenticationExtractor<MemberAuthRequest> {

    private static final String BASIC_TYPE = "Basic";
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    private final BasicAuthenticationDecoder basicAuthenticationDecoder;

    public BasicAuthenticationExtractor(final BasicAuthenticationDecoder basicAuthenticationDecoder) {
        this.basicAuthenticationDecoder = basicAuthenticationDecoder;
    }

    public MemberAuthRequest extract(final String header) {
        if (header == null) {
            throw new AuthException(NOT_EXIST_HEADER);
        }
        if (!header.toLowerCase().startsWith(BASIC_TYPE.toLowerCase())) {
            throw new AuthException(INVALID_HEADER);
        }
        final String[] credentials = basicAuthenticationDecoder.decode(header);
        final String email = credentials[EMAIL_INDEX];
        final String password = credentials[PASSWORD_INDEX];
        return new MemberAuthRequest(email, password);
    }
}
