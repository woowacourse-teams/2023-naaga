package com.now.naaga.place.application.dto;

import com.now.naaga.place.domain.Position;
import com.now.naaga.place.presentation.dto.CreatePlaceRequest;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.web.multipart.MultipartFile;

public record CreatePlaceCommand(Long playerId,
                                 String name,
                                 String description,
                                 Position position,
                                 MultipartFile imageFile) {

    public static CreatePlaceCommand of(final PlayerRequest playerRequest,
                                        final CreatePlaceRequest createPlaceRequest) {
        Position position = Position.of(createPlaceRequest.latitude(), createPlaceRequest.longitude());
        return new CreatePlaceCommand(
                playerRequest.playerId(),
                createPlaceRequest.name(),
                createPlaceRequest.description(),
                position,
                createPlaceRequest.imageFile()
        );
    }
}
