package com.now.naaga.game.application.dto;

import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.CreateGameRequest;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record CreateGameCommand(Long playerId,
                                Position playerPosition) {

    public static CreateGameCommand of(final PlayerRequest playerRequest,
                                       final CreateGameRequest createGameRequest) {
        final CoordinateRequest coordinate = createGameRequest.coordinate();
        return new CreateGameCommand(
                playerRequest.playerId(),
                coordinate.convertToPosition()
        );
    }
}
