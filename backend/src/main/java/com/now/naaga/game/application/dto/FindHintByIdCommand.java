package com.now.naaga.game.application.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindHintByIdCommand(Long hintId,
                                  Long gameId,
                                  Long playerId) {

    public static FindHintByIdCommand of(final PlayerRequest playerRequest,
                                         final Long gameId,
                                         final Long hintId) {
        return new FindHintByIdCommand(
                hintId,
                gameId,
                playerRequest.playerId());
    }
}
