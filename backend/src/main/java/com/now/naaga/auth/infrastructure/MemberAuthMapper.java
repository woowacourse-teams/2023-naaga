package com.now.naaga.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.common.exception.InternalException;
import com.now.naaga.common.exception.InternalExceptionType;
import org.springframework.stereotype.Component;

import static com.now.naaga.common.exception.InternalExceptionType.FAIL_JSON_TO_OBJECT;
import static com.now.naaga.common.exception.InternalExceptionType.FAIL_OBJECT_TO_JSON;

public class MemberAuthMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertMemberAuthToString(final MemberAuth memberAuth) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(memberAuth);
        } catch (JsonProcessingException e) {
            throw new InternalException(FAIL_OBJECT_TO_JSON);
        }
    }

    public static MemberAuth convertStringToMemberAuth(final String subject) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(subject, MemberAuth.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(FAIL_JSON_TO_OBJECT);
        }
    }
}
