package com.now.naaga.place.presentation.dto;

import java.util.Objects;

public record PlaceResponse(Long id,
                            String name,
                            CoordinateResponse coordinate,
                            String imageUrl,
                            String description) {

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlaceResponse that = (PlaceResponse) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(coordinate, that.coordinate)
                && Objects.equals(imageUrl, that.imageUrl)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinate, imageUrl, description);
    }

    @Override
    public String toString() {
        return "PlaceResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinate=" + coordinate +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
