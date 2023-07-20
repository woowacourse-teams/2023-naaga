package com.now.naaga.auth.infrastructure;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthenticationDecoder {

    private static final String BASIC_TYPE = "Basic";
    private static final String DELIMITER = ":";

    public String[] decode(final String header) {
        final String authHeaderValue = header.substring(BASIC_TYPE.length()).trim();
        final byte[] decodedBytes = Base64.decodeBase64(authHeaderValue);
        final String decodedString = new String(decodedBytes);

        return decodedString.split(DELIMITER);
    }
}

