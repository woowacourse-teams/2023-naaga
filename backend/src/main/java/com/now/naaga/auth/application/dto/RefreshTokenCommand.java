package com.now.naaga.auth.application.dto;

import com.now.naaga.auth.presentation.dto.RefreshTokenRequest;

public record RefreshTokenCommand(String refreshToken) {

    public static RefreshTokenCommand from(RefreshTokenRequest refreshTokenRequest) {
        return new RefreshTokenCommand(refreshTokenRequest.refreshToken());
    }
}
