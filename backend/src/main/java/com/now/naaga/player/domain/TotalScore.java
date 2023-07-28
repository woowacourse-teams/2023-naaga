package com.now.naaga.player.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import org.hibernate.annotations.ColumnDefault;

@Embeddable
public class TotalScore {

    @ColumnDefault(value = "0")
    @Column(name = "total_score")
    private int value = 0;

    protected TotalScore() {
    }

    public TotalScore(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TotalScore that = (TotalScore) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TotalScore{" +
                "value=" + value +
                '}';
    }
}
