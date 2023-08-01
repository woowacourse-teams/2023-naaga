package com.now.naaga.place.presentation.dto;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;

public record PlaceResponse(Long id,
                            Position position,
                            String imageUrl) {

    public static PlaceResponse from(final Place place) {
        return new PlaceResponse(place.getId(), place.getPosition(), place.getImageUrl());
    }
}
