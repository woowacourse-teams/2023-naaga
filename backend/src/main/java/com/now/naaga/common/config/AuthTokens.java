package com.now.naaga.common.config;

public class AuthTokens {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    public AuthTokens(final String accessToken,
                      final String refreshToken,
                      final String grantType,
                      final Long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
    }

    public AuthTokens() {
    }

    public static AuthTokens of(final String accessToken,
                                final String refreshToken,
                                final String grantType,
                                final Long expiresIn) {
        return new AuthTokens(accessToken, refreshToken, grantType, expiresIn);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getGrantType() {
        return grantType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
