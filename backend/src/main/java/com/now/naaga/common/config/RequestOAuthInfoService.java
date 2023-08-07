package com.now.naaga.common.config;

import org.springframework.stereotype.Component;

@Component
public class RequestOAuthInfoService {

    private final KakaoApiClient client;

    public RequestOAuthInfoService(final KakaoApiClient client) {
        this.client = client;
    }

    public KakaoInfoResponse request(KakaoLoginParams params) {
        String accessToken = client.requestAccessToken(params);
        return client.requestOauthInfo(accessToken);
    }
}
