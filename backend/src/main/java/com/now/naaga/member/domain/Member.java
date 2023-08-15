package com.now.naaga.member.domain;

import com.now.naaga.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Member extends BaseEntity {

    private static final String DELETED_EMAIL = "NONE";

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String email;

    private boolean deleted = false;

    protected Member() {
    }

    public Member(final String email) {
        this(null, email, false);
    }

    public Member(final Long id, final String email, final boolean deleted) {
        this.id = id;
        this.email = email;
        this.deleted = deleted;
    }

    public void delete() {
        this.deleted = true;
        this.email = DELETED_EMAIL;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
