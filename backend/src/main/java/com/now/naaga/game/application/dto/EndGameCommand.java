package com.now.naaga.game.application.dto;

import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record EndGameCommand(Long playerId, EndType endType, Position position, Long gameId) {

    public static EndGameCommand of(final PlayerRequest playerRequest, final EndGameRequest endGameRequest, final Long gameId) {
        return new EndGameCommand(playerRequest.playerId(),
                EndType.valueOf(endGameRequest.endType()),
                endGameRequest.coordinate().convertToPosition(),
                gameId);
    }


}
