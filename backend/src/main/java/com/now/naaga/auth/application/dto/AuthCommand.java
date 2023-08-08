package com.now.naaga.auth.application.dto;

import com.now.naaga.auth.infrastructure.auth.AuthType;
import com.now.naaga.auth.presentation.dto.AuthRequest;

public record AuthCommand(String token,
                          AuthType type) {
    public static AuthCommand from(final AuthRequest authRequest) {
        return new AuthCommand(
                authRequest.token(),
                AuthType.valueOf(authRequest.type().toUpperCase())
        );
    }
}
