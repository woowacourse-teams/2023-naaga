package com.now.naaga.auth.infrastructure;

import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.auth.exception.AuthExceptionType;
import com.now.naaga.auth.infrastructure.dto.UnlinkInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {

    private final RestTemplate restTemplate;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    @Value("${oauth.kakao.admin-key}")
    private String adminKey;

    public AuthClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AuthInfo requestOauthInfo(final String token) {
        final String url = apiUrl + "/v2/user/me";

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + token);

        final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.email\", \"kakao_account.profile\"]");

        final HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        try {
            return restTemplate.postForObject(url, request, AuthInfo.class);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionType.INVALID_KAKAO_INFO);
        }
    }

    //todo :
    public void requestUnlink(final Long authId) {
        final String url = apiUrl + "/v1/user/unlink";

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK " + adminKey);

        final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", String.valueOf(authId));

        final HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        try {
            restTemplate.postForObject(url, request, UnlinkInfo.class);
        } catch (Exception e) {
            throw new AuthException(AuthExceptionType.INVALID_KAKAO_DELETE);
        }
    }
}
