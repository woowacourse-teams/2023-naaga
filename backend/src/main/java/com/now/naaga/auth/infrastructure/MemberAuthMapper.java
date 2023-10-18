package com.now.naaga.auth.infrastructure;

import static com.now.naaga.common.exception.InternalExceptionType.FAIL_JSON_TO_OBJECT;
import static com.now.naaga.common.exception.InternalExceptionType.FAIL_OBJECT_TO_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.common.exception.InternalException;

public class MemberAuthMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertMemberAuthToString(final MemberAuth memberAuth) {
        try {
            return objectMapper.writeValueAsString(memberAuth);
        } catch (JsonProcessingException e) {
            throw new InternalException(FAIL_OBJECT_TO_JSON);
        }
    }

    public static MemberAuth convertStringToMemberAuth(final String subject) {
        try {
            return objectMapper.readValue(subject, MemberAuth.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(FAIL_JSON_TO_OBJECT);
        }
    }
}
