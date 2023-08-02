package com.now.naaga.place.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Position {

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    protected Position() {
    }

    public Position(final BigDecimal latitude,
                    final BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Position of(final Double latitude,
                              final Double longitude) {
        return new Position(new BigDecimal(latitude), new BigDecimal(longitude));
    }

    public double calculateDistance(final Position other) {
        return Math.acos(Math.sin(Math.toRadians(other.latitude.doubleValue())) * Math.sin(Math.toRadians(this.latitude.doubleValue()))
                + (Math.cos(Math.toRadians(other.latitude.doubleValue())) * Math.cos(Math.toRadians(this.latitude.doubleValue())) * Math.cos(
                Math.toRadians(other.longitude.doubleValue() - this.longitude.doubleValue())))
        ) * 6371.0;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Position position = (Position) o;
        return Objects.equals(latitude, position.latitude)
                && Objects.equals(longitude, position.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "Position{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
