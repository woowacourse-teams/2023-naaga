package com.now.naaga.auth.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.auth.application.AuthService;
import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.application.dto.RefreshTokenCommand;
import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.presentation.dto.AuthRequest;
import com.now.naaga.auth.presentation.dto.AuthResponse;
import com.now.naaga.auth.presentation.dto.MemberRequest;
import com.now.naaga.auth.presentation.dto.RefreshTokenRequest;
import com.now.naaga.member.presentation.dto.DeleteMemberCommand;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        final AuthToken authToken = authService.login(authCommand);
        final AuthResponse authResponse = AuthResponse.from(authToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshLogin(@RequestBody final RefreshTokenRequest refreshTokenRequest) {
        final RefreshTokenCommand refreshTokenCommand = RefreshTokenCommand.from(refreshTokenRequest);
        final AuthToken authToken = authService.refreshLogin(refreshTokenCommand);
        final AuthResponse authResponse = AuthResponse.from(authToken);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @DeleteMapping("/unlink")
    public ResponseEntity<Void> deleteAccount(@Auth MemberRequest memberRequest) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
