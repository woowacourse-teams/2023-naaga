package com.now.naaga.auth.domain;

import com.now.naaga.member.domain.Member;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;

    private String refreshToken;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    public AuthToken() {
    }

    public AuthToken(final String accessToken, final String refreshToken) {
        this(null, accessToken, refreshToken, null);
    }

    public AuthToken(final String accessToken, final String refreshToken, final Member member) {
        this(null, accessToken, refreshToken, member);
    }

    public AuthToken(final Long id, final String accessToken, final String refreshToken, final Member member) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.member = member;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthToken that = (AuthToken) o;
        return Objects.equals(accessToken, that.accessToken)
                && Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }

    @Override
    public String toString() {
        return "AuthTokens{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
