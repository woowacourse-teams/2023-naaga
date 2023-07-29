package com.now.naaga.place.application.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public record PlaceCommand(String name,
                           String description,
                           Double latitude,
                           Double longitude,
                           MultipartFile imageFile) {

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PlaceCommand that = (PlaceCommand) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(latitude, that.latitude)
                && Objects.equals(longitude, that.longitude)
                && Objects.equals(imageFile, that.imageFile);
    }

    @Override
    public String toString() {
        return "PlaceCommand{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", imageFile=" + imageFile +
                '}';
    }
}
