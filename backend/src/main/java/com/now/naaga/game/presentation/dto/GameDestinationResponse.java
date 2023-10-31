package com.now.naaga.game.presentation.dto;

import com.now.naaga.place.domain.Place;

public record GameDestinationResponse(Long id,
                                      String name,
                                      CoordinateResponse coordinate,
                                      String imageUrl,
                                      String description) {

    public static GameDestinationResponse from(final Place place) {
        return new GameDestinationResponse(place.getId(), place.getName(), CoordinateResponse.of(place.getPosition()), place.getImageUrl(), place.getDescription());
    }
}
