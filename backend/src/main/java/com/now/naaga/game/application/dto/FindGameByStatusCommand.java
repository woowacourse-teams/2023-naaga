package com.now.naaga.game.application.dto;

import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindGameByStatusCommand(Long playerId,
                                      GameStatus gameStatus) {

    public static FindGameByStatusCommand of(final PlayerRequest playerRequest,
                                             final String status) {
        final GameStatus gameStatus = GameStatus.valueOf(status.toUpperCase());
        return new FindGameByStatusCommand(
                playerRequest.playerId(),
                gameStatus
        );
    }
}
