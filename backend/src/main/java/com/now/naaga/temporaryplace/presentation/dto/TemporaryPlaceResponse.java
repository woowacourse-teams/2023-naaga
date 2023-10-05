package com.now.naaga.temporaryplace.presentation.dto;

import com.now.naaga.game.presentation.dto.CoordinateResponse;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;

import java.util.List;
import java.util.stream.Collectors;

public record TemporaryPlaceResponse(Long id,
                                     String name,
                                     CoordinateResponse coordinate,
                                     String imageUrl,
                                     String description,
                                     Long playerId

) {

    public static TemporaryPlaceResponse from(final TemporaryPlace savedTemporaryPlace) {
        CoordinateResponse coordinateResponse = CoordinateResponse.of(savedTemporaryPlace.getPosition());
        return new TemporaryPlaceResponse(
                savedTemporaryPlace.getId(),
                savedTemporaryPlace.getName(),
                coordinateResponse,
                savedTemporaryPlace.getImageUrl(),
                savedTemporaryPlace.getDescription(),
                savedTemporaryPlace.getRegisteredPlayer().getId()
        );
    }

    public static List<TemporaryPlaceResponse> convertToResponses(final List<TemporaryPlace> temporaryPlaces) {
        return temporaryPlaces.stream()
                .map(TemporaryPlaceResponse::from)
                .collect(Collectors.toList());
    }
}
