package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Game;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.presentation.dto.PlayerResponse;
import java.util.List;

public record GameResponse(Long id,
                           String startTime,
                           String gameStatus,
                           int remainingAttempts,
                           CoordinateResponse startCoordinate,
                           PlaceResponse destination,
                           PlayerResponse player,
                           List<HintResponse> hints) {

    public static GameResponse from(final Game game) {
        return new GameResponse(
                game.getId(),
                game.getStartTime().toString(),
                game.getGameStatus().name(),
                game.getRemainingAttempts(),
                CoordinateResponse.of(game.getStartPosition()),
                PlaceResponse.from(game.getPlace()),
                PlayerResponse.from(game.getPlayer()),
                HintResponse.convertToHintResponses(game.getHints())
        );
    }

    public static List<GameResponse> convertToGameResponses(final List<Game> games) {
        return games.stream()
                .map(GameResponse::from)
                .toList();
    }
}
