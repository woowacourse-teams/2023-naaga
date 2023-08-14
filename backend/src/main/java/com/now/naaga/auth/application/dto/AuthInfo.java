package com.now.naaga.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AuthInfo {

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public AuthInfo() {
    }

    public AuthInfo(final KakaoAccount kakaoAccount) {
        this.kakaoAccount = kakaoAccount;
    }

    public static AuthInfo of(final String email,
                              final String nickname) {
        final KakaoProfile kakaoProfile = new KakaoProfile(nickname);
        final KakaoAccount kakaoAccount = new KakaoAccount(kakaoProfile, email);
        return new AuthInfo(kakaoAccount);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {

        private KakaoProfile profile;

        private String email;

        public KakaoAccount() {
        }

        public KakaoAccount(final KakaoProfile profile,
                            final String email) {
            this.profile = profile;
            this.email = email;
        }

        public KakaoProfile getProfile() {
            return profile;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final KakaoAccount that = (KakaoAccount) o;
            return Objects.equals(profile, that.profile)
                    && Objects.equals(email, that.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(profile, email);
        }

        @Override
        public String toString() {
            return "KakaoAccount{" +
                    "profile=" + profile +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoProfile {

        private String nickname;

        public KakaoProfile() {
        }

        public KakaoProfile(final String nickname) {
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final KakaoProfile that = (KakaoProfile) o;
            return Objects.equals(nickname, that.nickname);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nickname);
        }

        @Override
        public String toString() {
            return "KakaoProfile{" +
                    "nickname='" + nickname + '\'' +
                    '}';
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return kakaoAccount.email;
    }

    public String getNickname() {
        return kakaoAccount.profile.nickname;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthInfo that = (AuthInfo) o;
        return Objects.equals(kakaoAccount, that.kakaoAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kakaoAccount);
    }

    @Override
    public String toString() {
        return "KakaoAuthInfo{" +
                "kakaoAccount=" + kakaoAccount +
                '}';
    }
}
