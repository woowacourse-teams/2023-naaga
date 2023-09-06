package com.now.naaga.game.application.dto;

import com.now.naaga.game.presentation.dto.CoordinateRequest;
import com.now.naaga.game.presentation.dto.CreateHintRequest;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record CreateHintCommand(Long gameId, Long playerId, Position coordinate) {
    
    public static CreateHintCommand of(final PlayerRequest playerRequest,
            final CreateHintRequest createHintRequest,
            final Long gameId) {
        return new CreateHintCommand(
                gameId,
                playerRequest.playerId(),
                createHintRequest.coordinate().convertToPosition());
    }
    
    public static CreateHintCommand ofCoordinate(final PlayerRequest playerRequest,
            final CoordinateRequest coordinateRequest,
            final Long gameId) {
        return new CreateHintCommand(
                gameId,
                playerRequest.playerId(),
                coordinateRequest.convertToPosition());
    }
}
