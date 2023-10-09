package com.now.naaga.place.application.dto;

import com.now.naaga.place.domain.Position;
import com.now.naaga.place.presentation.dto.CreatePlaceRequest;

public record CreatePlaceCommand(String name,
                                 String description,
                                 Position position,
                                 String imageUrl,
                                 Long registeredPlayerId,
                                 Long temporaryPlaceId) {

    public static CreatePlaceCommand from(final CreatePlaceRequest createPlaceRequest) {
        Position position = Position.of(createPlaceRequest.latitude(), createPlaceRequest.longitude());

        return new CreatePlaceCommand(
                createPlaceRequest.name(),
                createPlaceRequest.description(),
                position,
                createPlaceRequest.imageUrl(),
                createPlaceRequest.registeredPlayerId(),
                createPlaceRequest.temporaryPlaceId()
        );
    }
}
