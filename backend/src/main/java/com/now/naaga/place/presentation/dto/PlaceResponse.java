package com.now.naaga.place.presentation.dto;

import com.now.naaga.place.domain.Place;
import java.util.List;
import java.util.stream.Collectors;

public record PlaceResponse(Long id,
                            String name,
                            CoordinateResponse coordinate,
                            String imageUrl,
                            String description) {

    public static PlaceResponse from(final Place savedPlace) {
        return new PlaceResponse(
                savedPlace.getId(),
                savedPlace.getName(),
                CoordinateResponse.from(savedPlace.getPosition()),
                savedPlace.getImageUrl(),
                savedPlace.getDescription()
        );
    }

    public static List<PlaceResponse> convertToPlaceResponses(final List<Place> places) {
        return places.stream()
                .map(PlaceResponse::from)
                .collect(Collectors.toList());
    }
}
