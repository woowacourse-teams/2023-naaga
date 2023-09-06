package com.now.naaga.auth.presentation.dto;

import com.now.naaga.auth.domain.AuthToken;

public record AuthResponse(String accessToken,
                           String refreshToken) {

    public static AuthResponse from(final AuthToken authToken) {
        return new AuthResponse(
                authToken.getAccessToken(),
                authToken.getRefreshToken()
        );
    }
}
