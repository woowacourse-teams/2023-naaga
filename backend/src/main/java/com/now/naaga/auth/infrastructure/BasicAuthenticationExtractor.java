package com.now.naaga.auth.infrastructure;

import com.now.naaga.member.dto.MemberCommand;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthenticationExtractor implements AuthenticationExtractor<MemberCommand> {

    private static final String BASIC_TYPE = "Basic";
    private static final int EMAIL_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;

    private final BasicAuthenticationDecoder basicAuthenticationDecoder;

    public BasicAuthenticationExtractor(final BasicAuthenticationDecoder basicAuthenticationDecoder) {
        this.basicAuthenticationDecoder = basicAuthenticationDecoder;
    }

    public MemberCommand extract(String header) {
        if (header == null) {
            throw new RuntimeException("헤더가 존재하지 않습니다.");
        }
        if (!header.toLowerCase().startsWith(BASIC_TYPE.toLowerCase())) {
            throw new RuntimeException("헤더 정보가 잘못됐습니다.");
        }
        String[] credentials = basicAuthenticationDecoder.decode(header);
        String email = credentials[EMAIL_INDEX];
        String password = credentials[PASSWORD_INDEX];
        return new MemberCommand(email, password);
    }
}
