package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Game;
import com.now.naaga.place.presentation.dto.PlaceResponse;

public class GameResponse {

    private final Long id;
    private final PlaceResponse place;
    private final String gameStatus;

    private GameResponse(final Long id,
                         final PlaceResponse place,
                         final String gameStatus) {
        this.id = id;
        this.place = place;
        this.gameStatus = gameStatus;
    }

    public static GameResponse from(final Game game) {
        return new GameResponse(game.getId(), PlaceResponse.from(game.getPlace()), game.getGameStatus().toString());
    }

    public Long getId() {
        return id;
    }

    public PlaceResponse getPlace() {
        return place;
    }

    public String getGameStatus() {
        return gameStatus;
    }
}
