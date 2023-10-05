package com.now.naaga.temporaryplace.application.dto;

import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.temporaryplace.presentation.dto.CreateTemporaryPlaceRequest;
import org.springframework.web.multipart.MultipartFile;

public record CreateTemporaryPlaceCommand(Long playerId,
                                          String name,
                                          String description,
                                          Position position,
                                          MultipartFile imageFile) {

    public static CreateTemporaryPlaceCommand of(final PlayerRequest playerRequest,
                                                 final CreateTemporaryPlaceRequest createTemporaryPlaceRequest) {
        Position position = Position.of(createTemporaryPlaceRequest.latitude(), createTemporaryPlaceRequest.longitude());
        return new CreateTemporaryPlaceCommand(
                playerRequest.playerId(),
                createTemporaryPlaceRequest.name(),
                createTemporaryPlaceRequest.description(),
                position,
                createTemporaryPlaceRequest.imageFile());
    }
}
