package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Game;

public record GameStatusResponse(Long id,
                                 String gameStatus) {

    public static GameStatusResponse from(final Game game) {
        return new GameStatusResponse(game.getId(), game.getGameStatus().toString());
    }
}
