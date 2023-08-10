package com.now.naaga.auth.presentation.dto;

import com.now.naaga.auth.domain.AuthTokens;

public record AuthResponse(String accessToken,
                           String refreshToken) {

    public static AuthResponse from(final AuthTokens authTokens) {
        return new AuthResponse(
                authTokens.getAccessToken(),
                authTokens.getRefreshToken()
        );
    }
}
