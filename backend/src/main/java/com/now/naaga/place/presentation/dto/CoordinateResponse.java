package com.now.naaga.place.presentation.dto;

import com.now.naaga.place.domain.Position;

import java.math.RoundingMode;
import java.util.Objects;

public record CoordinateResponse(Double latitude,
                                 Double longitude) {

    public static CoordinateResponse from(final Position position) {
        return new CoordinateResponse(
                position.getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue(),
                position.getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CoordinateResponse that = (CoordinateResponse) o;
        return Objects.equals(latitude, that.latitude)
                && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return "CoordinateResponse{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
