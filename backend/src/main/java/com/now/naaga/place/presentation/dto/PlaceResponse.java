package com.now.naaga.place.presentation.dto;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;

public class PlaceResponse {

    private final Long id;
    private final Position position;
    private final String imageUrl;

    private PlaceResponse(final Long id, final Position position, final String imageUrl) {
        this.id = id;
        this.position = position;
        this.imageUrl = imageUrl;
    }

    public static PlaceResponse from(final Place place) {
        return new PlaceResponse(place.getId(), place.getPosition(), place.getImageUrl());
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
}
