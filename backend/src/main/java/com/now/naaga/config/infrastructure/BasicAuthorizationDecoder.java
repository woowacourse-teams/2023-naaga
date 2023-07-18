package com.now.naaga.config.infrastructure;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthorizationDecoder {

    private static final String BASIC_TYPE = "Basic";
    private static final String DELIMITER = ":";

    public String[] decode(String header) {
        String authHeaderValue = header.substring(BASIC_TYPE.length()).trim();
        byte[] decodedBytes = Base64.decodeBase64(authHeaderValue);
        String decodedString = new String(decodedBytes);

        return decodedString.split(DELIMITER);
    }
}

