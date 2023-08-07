package com.now.naaga.common.config;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class KakaoLoginParams {

    private String authorizationCode;

    public KakaoLoginParams() {
    }

    public MultiValueMap<String, String> makeBody() {
        final MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }
}
