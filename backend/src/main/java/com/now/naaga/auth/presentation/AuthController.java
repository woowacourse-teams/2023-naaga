package com.now.naaga.auth.presentation;

import com.now.naaga.auth.application.AuthService;
import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.domain.AuthTokens;
import com.now.naaga.auth.presentation.dto.AuthRequest;
import com.now.naaga.auth.presentation.dto.AuthResponse;
import com.now.naaga.auth.presentation.dto.RefreshTokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService; // 멤버

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<AuthResponse> login(@RequestBody final AuthRequest authRequest) {
        final AuthCommand authCommand = AuthCommand.from(authRequest);
        final AuthTokens authTokens = authService.login(authCommand);
        final AuthResponse authResponse = AuthResponse.from(authTokens);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshLogin(@RequestBody final RefreshTokenRequest refreshTokenRequest) {
        return null;
    }
}
