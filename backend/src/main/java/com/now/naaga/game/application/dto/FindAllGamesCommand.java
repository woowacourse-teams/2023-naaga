package com.now.naaga.game.application.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindAllGamesCommand(Long playerId) {
    
    public static FindAllGamesCommand of(PlayerRequest playerRequest) {
        return new FindAllGamesCommand(playerRequest.playerId());
    }
}
