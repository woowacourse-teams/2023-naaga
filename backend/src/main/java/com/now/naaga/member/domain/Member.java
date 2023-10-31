package com.now.naaga.member.domain;

import com.now.naaga.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@SQLDelete(sql = "UPDATE member SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Entity
public class Member extends BaseEntity {

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
