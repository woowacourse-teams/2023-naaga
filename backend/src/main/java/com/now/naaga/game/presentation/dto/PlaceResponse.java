package com.now.naaga.game.presentation.dto;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;

public class PlaceResponse {

    private Long id;
    private Position position;
    private String imageUrl;

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
