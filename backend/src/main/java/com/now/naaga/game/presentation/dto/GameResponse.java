package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Game;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import java.util.List;
import java.util.stream.Collectors;

public record GameResponse(Long id,
                           PlaceResponse place,
                           String gameStatus) {

    public static GameResponse from(final Game game) {
        return new GameResponse(game.getId(), PlaceResponse.from(game.getPlace()), game.getGameStatus().toString());
    }

    public static List<GameResponse> convertToGameResponses(final List<Game> games) {
        return games.stream()
                .map(GameResponse::from)
                .collect(Collectors.toList());
    }
}
