package com.now.naaga.auth.infrastructure.dto;

import com.now.naaga.auth.infrastructure.AuthType;
import java.util.Objects;

public class MemberAuth {

    private Long memberId;

    private Long authId;

    private AuthType authType;

    public MemberAuth() {
    }

    public MemberAuth(final Long memberId, final Long authId, final AuthType authType) {
        this.memberId = memberId;
        this.authId = authId;
        this.authType = authType;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getAuthId() {
        return authId;
    }

    public AuthType getAuthType() {
        return authType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MemberAuth that = (MemberAuth) o;
        return Objects.equals(memberId, that.memberId)
                && Objects.equals(authId, that.authId)
                && authType == that.authType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, authId, authType);
    }

    @Override
    public String toString() {
        return "MemberAuth{" +
                "memberId=" + memberId +
                ", authId=" + authId +
                ", authType=" + authType +
                '}';
    }
}
