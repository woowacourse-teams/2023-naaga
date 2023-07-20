package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Game;

public class GameStatusResponse {

    private final Long id;
    private final String gameStatus;

    private GameStatusResponse(final Long id,
                               final String gameStatus) {
        this.id = id;
        this.gameStatus = gameStatus;
    }

    public static GameStatusResponse from(final Game game) {
        return new GameStatusResponse(game.getId(), game.getGameStatus().toString());
    }

    public Long getId() {
        return id;
    }

    public String getGameStatus() {
        return gameStatus;
    }
}
