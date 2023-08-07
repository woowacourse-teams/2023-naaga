package com.now.naaga.common.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final OAuthLoginService oAuthLoginService;

    public AuthController(final OAuthLoginService oAuthLoginService) {
        this.oAuthLoginService = oAuthLoginService;
    }

    @PostMapping
    public ResponseEntity<AuthTokens> login(@RequestBody KakaoLoginParams params) {
        System.out.println("controller = " + params.getAuthorizationCode());
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }
}
