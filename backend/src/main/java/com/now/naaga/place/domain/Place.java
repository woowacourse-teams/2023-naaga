package com.now.naaga.place.domain;

import jakarta.persistence.*;

import java.util.Objects;

import static com.now.naaga.game.domain.Game.MIN_RANGE;

@Entity
public class Place {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Embedded
    private Position position;

    private String imageUrl;

    protected Place() {
    }

    public boolean isInValidRange(final Position other) {
        return position.calculateDistance(other) <= MIN_RANGE;
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Place place = (Place) o;
        return Objects.equals(id, place.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", position=" + position +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
