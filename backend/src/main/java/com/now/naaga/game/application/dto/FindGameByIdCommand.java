package com.now.naaga.game.application.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindGameByIdCommand(Long gameId,
                                  Long playerId) {

    public static FindGameByIdCommand of(final PlayerRequest playerRequest,
                                         final Long gameId) {
        return new FindGameByIdCommand(gameId, playerRequest.playerId());
    }
}
