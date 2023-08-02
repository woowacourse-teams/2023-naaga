package com.now.naaga.game.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.now.naaga.game.domain.Game;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameStatusResponse(Long id,
                                 String gameStatus) {

    public static GameStatusResponse from(final Game game) {
        return new GameStatusResponse(game.getId(), game.getGameStatus().toString());
    }
}
