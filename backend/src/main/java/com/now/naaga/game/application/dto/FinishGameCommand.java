package com.now.naaga.game.application.dto;

import com.now.naaga.game.presentation.dto.FinishGameRequest;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FinishGameCommand(Long gameId,
                                Long playerId,
                                Position position) {

    public static FinishGameCommand of(final PlayerRequest playerRequest,
                                       final FinishGameRequest finishGameRequest,
                                       final Long gameId) {
        return new FinishGameCommand(
                gameId,
                playerRequest.playerId(),
                finishGameRequest.coordinate().convertToPosition()
        );
    }
}
